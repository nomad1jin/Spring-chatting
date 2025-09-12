package practice.chatserver.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.dto.ChatResDTO;
import practice.chatserver.chat.repository.ChatRoomRepository;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.service.AuthCommandService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final ChatParticipantService chatParticipantService;
    private final AuthCommandService authCommandService;

    @Transactional
    public ChatResDTO.ChatRoomCreatedDTO createRoom(Long initiatorId, Long targetId, String roomName) {
        // 사용자 검증
        Member initiator = authCommandService.findById(initiatorId);
        Member target = authCommandService.findById(targetId);

        // 중복방 체크하고 없으면 생성
        Optional<ChatRoom> existingRoom = findExistingRoom(initiatorId, targetId);
        if(existingRoom.isPresent()) {
            ChatRoom chatRoom = existingRoom.get();
            return new ChatResDTO.ChatRoomCreatedDTO(chatRoom.getId(), chatRoom.getRoomName());
        }
        ChatRoom chatRoom = makeChatRoom(roomName);

        // 두 참여자 모두 추가
        List<ChatParticipant> participants = Arrays.asList(
                ChatParticipant.builder().chatRoom(chatRoom).member(initiator).build(),
                ChatParticipant.builder().chatRoom(chatRoom).member(target).build()
        );
        chatParticipantService.saveAllParticipants(participants);

        return new ChatResDTO.ChatRoomCreatedDTO(chatRoom.getId(), chatRoom.getRoomName());
    }


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

    public Optional<ChatRoom> findExistingRoom(Long initiatorId, Long targetId) {
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
