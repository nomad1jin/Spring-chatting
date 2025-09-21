package practice.chatserver.chat.stomp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.service.ChatMessageService;
import practice.chatserver.global.redis.service.RedisPubSubService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatMessageService chatMessageService;
    private final RedisPubSubService redisPubSubService;
    private final ObjectMapper objectMapper;

    // 방법1. MessageMapping(수신)과 SendTo(발행) 한꺼번에 처리
    @MessageMapping("/room/{roomId}/sendto")
    @SendTo("/topic/{roomId}")
    public void sendMessage1(@DestinationVariable Long roomId, ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO){
        log.info("[ sendMessage1 : {} ]", chatMessageReqDTO.getMessage());
        chatMessageService.saveMessage(chatMessageReqDTO);
    }

    // 방법2. MessageMapping만 활용 - 현재 이거 활용
    // app/room/1 에서 메세지 매핑
    @MessageMapping("/room/{roomId}")
    public void sendMessage2(@DestinationVariable Long roomId, ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO)
            throws JsonProcessingException {
        log.info("[ sendMessage2 : {} ]", chatMessageReqDTO.getMessage());
        chatMessageService.saveMessage(chatMessageReqDTO);

//        String message = objectMapper.writeValueAsString(chatMessageReqDTO);
        redisPubSubService.publish("chat", chatMessageReqDTO);
        // 레디스 chat 채널로 해당 메세지 발행하면
        // 메세지리스너어댑터가 메세지를 수신하여 펍섭서비스의 onMessage()을 호출
    }

}
