package practice.chatserver.chat.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import practice.chatserver.chat.service.ChatService;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.global.jwt.JwtUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Value("${jwt.secret}")
    private String secretKey;
    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("[ connect 토큰 유효성 검증 ]");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String accessToken = bearerToken.substring(7);
            jwtUtil.isValid(accessToken);
            String username = jwtUtil.getUsername(accessToken);
            log.info("[ Destination ]: {}", accessor.getDestination());
            String roomId = accessor.getDestination().split("/")[2];  //목적지 헤더

            if(!chatService.isRoomParticipant(username, Long.parseLong(roomId))){
                throw new CustomException(ErrorCode.ROOM_NO_AUTH);
            }

        }
        return message;
    }
}
