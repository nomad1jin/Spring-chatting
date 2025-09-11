package practice.chatserver.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.dto.ChatResDTO;
import practice.chatserver.chat.repository.ChatRoomRepository;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.service.AuthCommandService;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ChatParticipantService chatParticipantService;
    private final AuthCommandService authCommandService;

    public ChatRoom getChatRoom(Long roomId) {
        ChatRoom chatroom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOTFOUND));
        return chatroom;
    }

    // 개별 채팅방 카드 (이름, 메세지, 미읽음 수) 조회 -> 알림 보낼때 재활용할 생각
    public ChatResDTO.ChatRoomCardDTO getChatRoomCard(Long roomId, Long memberId) {
        ChatRoom chatRoom = getChatRoom(roomId);

        Member loginMember = authCommandService.findById(memberId);
        Member targetMember = chatParticipantService.getTargetMember(chatRoom, loginMember);
        Long unreadCount = chatMessageService.getUnreadCount(chatRoom, loginMember);
        ChatMessage latestMessage = chatMessageService.getLatestMessage(chatRoom);

        return new ChatResDTO.ChatRoomCardDTO(
                chatRoom.getId(),
                chatRoom.getRoomName(),
                targetMember.getNickname(),
                latestMessage.getChatMessage(),
                unreadCount);
    }


    // 채팅방 카드 리스트(이름, 메세지, 미읽음 수) 조회
    public List<ChatResDTO.ChatRoomCardDTO> getChatRoomCards(Long memberId) {
        List<ChatRoom> rooms = chatRoomRepository.findRoomIdsByMemberId(memberId);

        return rooms.stream()
                .map(room -> getChatRoomCard(room.getId(), memberId))  // 개별 메서드 재사용
                .toList();
    }

    public ChatRoom findExistingRoom(Long initiatorId, Long targetId) {
        return chatRoomRepository.findExistingRoom(initiatorId, targetId);
    }

    public ChatRoom makeChatRoom(String roomName) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
}
