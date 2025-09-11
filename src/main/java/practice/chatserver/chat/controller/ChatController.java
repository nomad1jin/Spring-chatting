package practice.chatserver.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.dto.ChatResDTO;

import practice.chatserver.chat.service.ChatRoomService;
import practice.chatserver.chat.service.ChatService;
import practice.chatserver.global.apiPayload.CustomResponse;
import practice.chatserver.global.apiPayload.code.SuccessCode;
import practice.chatserver.global.jwt.CustomUserDetails;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방", description = "채팅방 생성하기 ")
    @PostMapping("/room/create")
    public CustomResponse<?> createRoom(@RequestBody ChatReqDTO.ChatRoomCreateDTO dto,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.createRoom(userDetails.getId(), dto.getTargetId(), dto.getRoomName());
        return CustomResponse.onSuccess(SuccessCode.CREATED);
    }

    @Operation(summary = "채팅방 카드 단일 조회", description = "채팅방 카드 단일 조회하기 ")
    @GetMapping("/room/card")
    public CustomResponse<ChatResDTO.ChatRoomCardDTO> getRoomCard(@RequestBody ChatReqDTO.ChatRoomCardDTO dto,
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChatResDTO.ChatRoomCardDTO chatRoom = chatRoomService.getChatRoomCard(dto.getRoomId(), userDetails.getId());
        return CustomResponse.onSuccess(SuccessCode.OK, chatRoom);
    }

    @Operation(summary = "채팅방 목록 조회", description = "채팅방 전체 목록 조회하기 ")
    @GetMapping("/room/list")
    public CustomResponse<List<ChatResDTO.ChatRoomCardDTO>> getRoomCardList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatResDTO.ChatRoomCardDTO> chatRooms = chatRoomService.getChatRoomCards(userDetails.getId());
        return CustomResponse.onSuccess(SuccessCode.OK, chatRooms);
    }
//
//    @Operation(summary = "이전 메시지 조회", description = "이전 메시지 조회하기")
//    @GetMapping("/history/{roomId}")
//    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId) {
//        List<ChatResDTO.ChatHistoryListResDTO> chatMessageList = chatService.getChatHistory(roomId);
//        return ResponseEntity.ok(chatMessageList);
//    }

//    @PostMapping("/room/{roomId}/read")
//    public ResponseEntity<?> readMessage(@PathVariable Long roomId) {
//        chatService.readMessage(roomId);
//        return ResponseEntity.ok().build();
//    }

}
