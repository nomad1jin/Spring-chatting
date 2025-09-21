package practice.chatserver.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void saveMessage(ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO) {
        //채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageReqDTO.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOTFOUND));

        //채팅방과 멤버 조회
        ChatParticipant sender = chatParticipantService.findByMemberIdAndChatRoomId(
                chatMessageReqDTO.getMemberId(), chatRoom.getId());

        //메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .participant(sender)
                .member(sender.getMember())
                .chatMessage(chatMessageReqDTO.getMessage())
                .build();
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 읽음여부 저장
        List<ChatParticipant> participants = chatParticipantService.findByChatRoomId(chatRoom.getId());
        for (ChatParticipant participant : participants) {
            ChatReadStatus readStatus = ChatReadStatus.builder()
                    .chatRoom(chatRoom)
                    .chatMessage(savedMessage)
                    .member(participant.getMember())
                    .isRead(participant.getMember().equals(sender.getMember()))
                    .build();
            chatReadStatusRepository.save(readStatus);
        }

    }

    public ChatMessage getLatestMessage(ChatRoom room) {
        return chatMessageRepository.findTop1ByChatRoomOrderByIdDesc(room)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    public Long getUnreadCount(ChatRoom room, Member member) {
        return chatReadStatusRepository.countByChatRoomAndMemberAndIsReadFalse(room, member);
    }

    public List<ChatResDTO.ChatMessageResDTO> getChatMessages(Long roomId, Long memberId) {
        // 로그인된 사용자가 참여자가 맞는지 확인
        ChatParticipant participants = chatParticipantService.findByMemberIdAndChatRoomId(memberId, roomId);
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdOrderByCreatedTimeAsc(roomId);
        List<ChatResDTO.ChatMessageResDTO> Dtos = new ArrayList<>();
        for (ChatMessage c : chatMessages) {

            boolean isRead;

            if (c.getMember().getId().equals(memberId)) {
                // 내가 보낸 메시지 → 상대방이 읽었는지 확인
                isRead = c.getChatReadStatuses().stream()
                        .filter(readStatus -> !readStatus.getMember().getId().equals(memberId)) // 나 제외
                        .allMatch(ChatReadStatus::isRead); // 모든 상대방이 읽었는지
            } else {
                // 상대방이 보낸 메시지 → 내가 읽었는지 확인
                isRead = c.getChatReadStatuses().stream()
                        .filter(readStatus -> readStatus.getMember().getId().equals(memberId))
                        .findFirst()
                        .map(ChatReadStatus::isRead)
                        .orElse(false);
            }

            ChatResDTO.ChatMessageResDTO dto = ChatResDTO.ChatMessageResDTO.builder()
                    .messageId(c.getId())
                    .senderId(c.getMember().getId())      //메세지에서 id를 꺼내야 메세지 보낸 사람
                    .senderName(c.getMember().getUsername())
                    .message(c.getChatMessage())
                    .isRead(isRead)
                    .build();
            Dtos.add(dto);
        }
        return Dtos;
    }

//    // 읽음 여부 바꿈
//    public void messageRead(Long roomId, Long memberId){
//        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
//                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOTFOUND));
//
//        Member member = authCommandService.findById(memberId);
//        markAsRead(chatRoom, member);
//    }
//
//    // 채팅방에 속해있으면서 memberNot에 reader(나)을 넣으면 내가 아닌 사람이 읽지않은 경우를 가져옴
//    // select 쿼리 1번 - 모든 안 읽은 메세지 조회
//    // update 쿼리 100번 - 메세지마다 개별 update
//    public void markAsRead(ChatRoom chatRoom, Member member) {
//        List<ChatMessage> unreadMessages =
//                chatMessageRepository.findByChatRoomAndMemberNotAndIsReadFalse(chatRoom, member);
//        for (ChatMessage chatMessage : unreadMessages) {
//            chatMessage.markAsRead();
//        }
//    }

    // 위의 메서드 리팩토링 - 조회없이 update 쿼리 1번
    public int markAsReadCount(Long roomId, Long memberId) {
        int updatedCount = chatReadStatusRepository.markMessagesAsRead(roomId, memberId);
        log.info("[ markAsRead count ] : {}", updatedCount);
        return updatedCount;
    }



}
