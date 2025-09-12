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
import practice.chatserver.chat.service.ChatRoomService;
import practice.chatserver.chat.service.ChatService;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.global.jwt.JwtUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        if(accessor.getCommand() == StompCommand.CONNECT) {
            // CONNECT 시점에는 사용자 인증만 확인
            String accessToken = extractAccessToken(accessor);
            String username = jwtUtil.getUsername(accessToken);
            log.info("[ STOMP 연결 성공 - 사용자: {} ]", username);

            // 사용자 정보를 세션에 저장 (웹소켓 세션 메모리에만 존재해서 서버 재시작하면 날아감)
            accessor.getSessionAttributes().put("username", username);
            accessor.setUser(() -> username);  // Principal 생성
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

                if (!chatService.isRoomParticipant(username, roomId)) {
                    log.error("[ 방 {} 참여 권한 없음 - 사용자: {} ]", roomId, username);
                    throw new CustomException(ErrorCode.ROOM_NO_AUTH);
                }
                log.info("[ 방 {} 구독 성공 - 사용자: {} ]", roomId, username);
            }
        }
        return message;
    }

    private String extractAccessToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (bearerToken == null || !jwtUtil.isValid(bearerToken.substring(7))) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return bearerToken.substring(7);
    }
}
