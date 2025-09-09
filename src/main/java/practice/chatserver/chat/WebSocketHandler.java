package practice.chatserver.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import practice.chatserver.chat.service.ChatService;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//connect로 웹소켓 연결요청이 들어왔을 때 이를 처리할 클래스
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("[ Connection Established ] {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("[ 메시지 수신 ] {}: {}", session.getId(), payload);
        // 받은 메시지를 모든 세션에 전송 (브로드캐스트)
        for(WebSocketSession s : sessions){
            if(s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
//                chatService.saveMessage()
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("[ Connection Closed ] {}", session.getId());
    }

}
