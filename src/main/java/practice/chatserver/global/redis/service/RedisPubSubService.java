package practice.chatserver.global.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import practice.chatserver.chat.dto.ChatReqDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPubSubService implements MessageListener {

    private final RedisTemplate redisTemplate;
//    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String payload = new String(message.getBody());
        log.info("Redis 메시지 수신 - 채널: {}, 메시지: {}", channel, payload);

        try {

//            String actualPayload = objectMapper.readValue(payload, String.class);
//            log.info("이스케이프 해제 후: {}", actualPayload);

            // 구독하고 있는 입장은 역직렬화해야하므로 readValue
            ChatReqDTO.ChatMessageReqDTO messageReqDTO = objectMapper.readValue(payload, ChatReqDTO.ChatMessageReqDTO.class);
            log.info("메시지 역직렬화 성공 - 방: {}, 발신자: {}, 내용: {}",
                    messageReqDTO.getRoomId(), messageReqDTO.getMemberId(), messageReqDTO.getMessage());

            // 직렬화하여 json으로 stomp에 publish (이때 경로는 반드시 /topic)
            String stompMessage = objectMapper.writeValueAsString(messageReqDTO);
            redisTemplate.convertAndSend("/topic/" + messageReqDTO.getRoomId(), stompMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
