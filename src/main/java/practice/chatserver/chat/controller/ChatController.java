package practice.chatserver.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import practice.chatserver.chat.domain.ChatParticipation;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.dto.ChatResDTO;
import practice.chatserver.chat.service.ChatService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "그룹채팅방", description = "그룹 채팅방 생성하기 ")
    @PostMapping("/room/group/create")
    public ResponseEntity<?> createRoom(@RequestParam String roomName) {
        chatService.createGroupRoom(roomName);
        return ResponseEntity.ok().build();
    }

//    @Operation(summary = "채팅방 목록 조회", description = "채팅방 목록 조회하기 ")
//    @GetMapping("/room/group/list")
//    public ResponseEntity<?> getRoomGroupList() {
//        List<ChatResDTO.ChatRoomListResDTO> chatRooms = chatService.getGroupChatRooms();
//        return ResponseEntity.ok(chatRooms);
//    }
//
//    @Operation(summary = "이전 메시지 조회", description = "이전 메시지 조회하기")
//    @GetMapping("/history/{roomId}")
//    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId) {
//        List<ChatResDTO.ChatHistoryListResDTO> chatMessageList = chatService.getChatHistory(roomId);
//        return ResponseEntity.ok(chatMessageList);
//    }

    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> readMessage(@PathVariable Long roomId) {
        chatService.readMessage(roomId);
        return ResponseEntity.ok().build();
    }

}
