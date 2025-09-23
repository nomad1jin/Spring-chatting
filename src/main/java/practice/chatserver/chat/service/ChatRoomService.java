package practice.chatserver.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private final MongoTemplate mongoTemplate;

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
                ChatParticipant.builder().roomId(chatRoom.getId()).memberId(initiatorId).build(),
                ChatParticipant.builder().roomId(chatRoom.getId()).memberId(targetId).build()
        );
        chatParticipantService.saveAllParticipants(participants);

        return new ChatResDTO.ChatRoomCreatedDTO(chatRoom.getId(), chatRoom.getRoomName());
    }


    public ChatRoom getChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOTFOUND));
    }

    // 개별 채팅방 카드 (이름, 메세지, 미읽음 수) 조회 -> 알림 보낼때 재활용할 생각
    public ChatResDTO.ChatRoomCardDTO getChatRoomCard(String roomId, Long memberId) {
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
//        List<ChatRoom> rooms = chatRoomRepository.findRoomIdsByMemberId(memberId);
        // 기존 코드는 jpa가 룸과 멤버를 조인하기에 쉽게 사용할 수 있었는데, 몽고디비에선 그럴 수 없고
        // ChatRoom에 memberId 필드를 추가하면 참여자 테이블과 데이터가 중복되고 동기화 문제가 생겨서 2번 거쳐서 조회하기로 결정
        Query query = Query.query(Criteria.where("memberId").is(memberId));
        List<ChatParticipant> participants = mongoTemplate.find(query, ChatParticipant.class);

        return participants.stream()
                .map(participant -> getChatRoomCard(participant.getRoomId(), memberId))
                .toList();
    }

    // 두 사용자가 모두 참여한 방 찾기
    public Optional<ChatRoom> findExistingRoom(Long initiatorId, Long targetId) {
//        return chatRoomRepository.findExistingRoom(initiatorId, targetId);
        // 간편한 JPA 사용할 수 없고 쿼리 두개로 공통 룸을 찾는 방식
        Query query1 = new Query(Criteria.where("memberId").is(initiatorId));
        List<ChatParticipant> initiatorRooms = mongoTemplate.find(query1, ChatParticipant.class);

        Query query2 = new Query(Criteria.where("memberId").is(targetId));
        List<ChatParticipant> targetRooms = mongoTemplate.find(query2, ChatParticipant.class);

        String commonRoomId = initiatorRooms.stream()
                .filter(p1 -> targetRooms.stream()
                        .anyMatch(p2 -> p1.getRoomId().equals(p2.getRoomId())))
                .map(ChatParticipant::getRoomId)
                .findFirst()
                .orElse(null);

        if(commonRoomId != null) {
            return chatRoomRepository.findById(commonRoomId);
        }

        return Optional.empty();
    }

    public ChatRoom makeChatRoom(String roomName) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .build();
        return chatRoomRepository.save(chatRoom);
    }
}
