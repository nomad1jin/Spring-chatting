//package practice.chatserver.chat;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import practice.chatserver.chat.domain.ChatMessage;
//import practice.chatserver.chat.service.ChatService;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//@RequestMapping("/chat")
//public class WebSocketController {
//
//    // 이건 스톰프임
//    private final SimpMessageSendingOperations messagingTemplate;
//    private final ChatService chatService;
//    private final SimpMessagingTemplate simpMessagingTemplate;
//
//    @MessageMapping("/roomId")
//    public void sendMessage(ChatMessage message){
//        // 메시지 전송
//        simpMessagingTemplate.convertAndSend("/topic/roomId",message);
//        log.debug("[ 메시지 전송 완료: roomId={}, sender={}", message.getParticipation().getChatRoom(), message.getParticipation().getChatMessages());
//
//        // 메시지 저장
//
//    }
//}
