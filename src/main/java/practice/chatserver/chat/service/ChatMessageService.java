package practice.chatserver.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatReadStatus;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.dto.ChatResDTO;
import practice.chatserver.chat.repository.ChatMessageRepository;
import practice.chatserver.chat.repository.ChatReadStatusRepository;
import practice.chatserver.chat.repository.ChatRoomRepository;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.service.AuthCommandService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatParticipantService chatParticipantService;
    private final AuthCommandService authCommandService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final MongoTemplate mongoTemplate;

    public void saveMessage(ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO) {
        //채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageReqDTO.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOTFOUND));

        //채팅방과 멤버 조회
        ChatParticipant sender = chatParticipantService.findByMemberIdAndRoomId(
                chatMessageReqDTO.getMemberId(), chatRoom.getId());

        //메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(chatRoom.getId())
                .participantId(sender.getId())
                .memberId(sender.getMemberId())
                .chatMessage(chatMessageReqDTO.getMessage())
                .build();
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 읽음여부 저장
        List<ChatParticipant> participants = chatParticipantService.findByChatRoomId(chatRoom.getId());
        for (ChatParticipant participant : participants) {
            ChatReadStatus readStatus = ChatReadStatus.builder()
                    .roomId(chatRoom.getId())
                    .messageId(savedMessage.getId())
                    .memberId(participant.getMemberId())
                    .isRead(participant.getMemberId().equals(sender.getMemberId()))
                    .build();
            chatReadStatusRepository.save(readStatus);
        }

    }

    public ChatMessage getLatestMessage(ChatRoom room) {
        return chatMessageRepository.findFirstByRoomIdOrderByCreatedTimeDesc(room.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    public Long getUnreadCount(ChatRoom room, Member member) {
        return chatReadStatusRepository.countByRoomIdAndMemberIdAndIsReadFalse(room.getId(), member.getId());
    }

    public List<ChatResDTO.ChatMessageResDTO> getChatMessages(String roomId, Long memberId) {
        // 로그인된 사용자가 참여자가 맞는지 확인
        chatParticipantService.findByMemberIdAndRoomId(memberId, roomId);
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderByCreatedTimeAsc(roomId);
        List<ChatResDTO.ChatMessageResDTO> Dtos = new ArrayList<>();

        for (ChatMessage c : chatMessages) {
            boolean isRead;

            if (c.getMemberId().equals(memberId)) {
                // 내가 보낸 메시지 → 상대방이 읽었는지 확인
                Query query = new Query(Criteria.where("messageId").is(c.getId())
                        .and("memberId").ne(memberId));
                List<ChatReadStatus> otherReadStatuses = mongoTemplate.find(query, ChatReadStatus.class);
                isRead = otherReadStatuses.stream().allMatch(ChatReadStatus::isRead);
            } else {
                // 상대방이 보낸 메시지 → 내가 읽었는지 확인
                Query query = new Query(Criteria.where("messageId").is(c.getId())
                        .and("memberId").is(memberId));
                ChatReadStatus myReadStatus = mongoTemplate.findOne(query, ChatReadStatus.class);
                isRead = myReadStatus.isRead();
            }

            Member sender = authCommandService.findById(c.getMemberId());

            ChatResDTO.ChatMessageResDTO dto = ChatResDTO.ChatMessageResDTO.builder()
                    .messageId(c.getId())
                    .senderId(c.getMemberId())      //메세지에서 id를 꺼내야 메세지 보낸 사람
                    .senderName(sender.getUsername())
                    .message(c.getChatMessage())
                    .isRead(isRead)
                    .build();
            Dtos.add(dto);
        }
        return Dtos;
    }

    public int markAsReadCount(String roomId, Long memberId) {
        //mongoTemplate
        Query query = new Query(Criteria.where("roomId").is(roomId)
                .and("memberId").is(memberId)
                .and("isRead").is(false));
        Update update = new Update().set("isRead", true);

        long updatedCount = mongoTemplate.updateMulti(query, update, ChatReadStatus.class).getModifiedCount();
        log.info("[ markAsRead count ] : {}", updatedCount);
        return (int) updatedCount;
    }

}
