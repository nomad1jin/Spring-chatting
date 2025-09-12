package practice.chatserver.chat.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.dto.ChatResDTO;
import practice.chatserver.chat.service.ChatService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

    // 방법1. MessageMapping(수신)과 SendTo(발행) 한꺼번에 처리
    @MessageMapping("/room/{roomId}/sendto")
    @SendTo("/topic/{roomId}")
    public void sendMessage1(@DestinationVariable Long roomId, ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO){
        log.info("[ sendMessage1 : {} ]", chatMessageReqDTO.getMessage());
        chatService.saveMessage(chatMessageReqDTO);
    }

    // 방법2. MessageMapping만 활용
    @MessageMapping("/room/{roomId}")
    public void sendMessage2(@DestinationVariable Long roomId, ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO){
        log.info("[ sendMessage2 : {} ]", chatMessageReqDTO.getMessage());
        chatService.saveMessage(chatMessageReqDTO);
        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageReqDTO.getMessage());
    }

}
