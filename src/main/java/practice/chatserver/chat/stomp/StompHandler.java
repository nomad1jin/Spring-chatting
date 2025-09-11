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

        if(accessor.getCommand() == StompCommand.CONNECT) {
            log.info("[ connect 토큰 유효성 검증 ]");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String accessToken = bearerToken.substring(7);
            jwtUtil.isValid(accessToken);
            String username = jwtUtil.getUsername(accessToken);

            // CONNECT 시점에는 사용자 인증만 확인
            log.info("[ STOMP 연결 성공 - 사용자: {} ]", username);

            // 사용자 정보를 세션에 저장
            accessor.getSessionAttributes().put("username", username);
        }
        else if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
            log.info("[ STOMP SUBSCRIBE - 방 참여 권한 검증 ]");
            String destination = accessor.getDestination();
            log.info("[ Subscribe Destination ]: {}", destination);

            if (destination != null && destination.startsWith("/topic/")) {
                // destination에서 roomId 추출: /topic/{roomId}
                String[] parts = destination.split("/");
                Long roomId = Long.parseLong(parts[2]);
                String username = (String) accessor.getSessionAttributes().get("username");

//                if (username != null && !chatService.isRoomParticipant(username, roomId)) {
//                    log.error("[ 방 {} 참여 권한 없음 - 사용자: {} ]", roomId, username);
//                    throw new CustomException(ErrorCode.ROOM_NO_AUTH);
//                }
                log.info("[ 방 {} 구독 성공 - 사용자: {} ]", roomId, username);
            }
        }
        return message;
    }
}
