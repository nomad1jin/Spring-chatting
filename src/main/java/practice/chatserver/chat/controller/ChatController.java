package practice.chatserver.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.dto.ChatResDTO;

import practice.chatserver.chat.service.ChatMessageService;
import practice.chatserver.chat.service.ChatRoomService;
import practice.chatserver.global.apiPayload.CustomResponse;
import practice.chatserver.global.apiPayload.code.SuccessCode;
import practice.chatserver.global.jwt.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방", description = "채팅방 생성하기 ")
    @PostMapping("/room/create")
    public CustomResponse<ChatResDTO.ChatRoomCreatedDTO> createRoom(@RequestBody ChatReqDTO.ChatRoomCreateDTO dto,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChatResDTO.ChatRoomCreatedDTO room = chatRoomService.createRoom(userDetails.getId(), dto.getTargetId(), dto.getRoomName());
        return CustomResponse.onSuccess(SuccessCode.CREATED, room);
    }

    @Operation(summary = "채팅방 카드 단일 조회", description = "채팅방 카드 단일 조회하기 ")
    @GetMapping("/room/card")
    public CustomResponse<ChatResDTO.ChatRoomCardDTO> getRoomCard(@RequestParam Long roomId,
                                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChatResDTO.ChatRoomCardDTO chatRoom = chatRoomService.getChatRoomCard(roomId, userDetails.getId());
        return CustomResponse.onSuccess(SuccessCode.OK, chatRoom);
    }

    @Operation(summary = "채팅방 목록 조회", description = "채팅방 전체 목록 조회하기 ")
    @GetMapping("/room/list")
    public CustomResponse<List<ChatResDTO.ChatRoomCardDTO>> getRoomCardList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatResDTO.ChatRoomCardDTO> chatRooms = chatRoomService.getChatRoomCards(userDetails.getId());
        return CustomResponse.onSuccess(SuccessCode.OK, chatRooms);
    }

    @Operation(summary = "채팅방 내 메시지 전체 조회", description = "채팅방 내 메시지 전체 조회하기")
    @GetMapping("/history/{roomId}")
    public CustomResponse<List<ChatResDTO.ChatMessageResDTO>> getChatHistory(@PathVariable Long roomId,
                                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatResDTO.ChatMessageResDTO> chatMessageList = chatMessageService.getChatMessages(roomId, userDetails.getId());
        return CustomResponse.onSuccess(SuccessCode.OK, chatMessageList);
    }

}
