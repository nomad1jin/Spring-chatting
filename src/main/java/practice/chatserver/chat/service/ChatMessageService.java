package practice.chatserver.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.repository.ChatMessageRepository;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.service.AuthCommandService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomService chatRoomService;
    private final ChatParticipantService chatParticipantService;
    private final AuthCommandService authCommandService;
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO) {
        //채팅방 조회
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatMessageReqDTO.getRoomId());

        //참여자 조회
        ChatParticipant sender = chatParticipantService.findByMemberId(chatMessageReqDTO.getMemberId());

        //메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .participant(sender)
                .member(sender.getMember())
                .chatMessage(chatMessageReqDTO.getMessage())
                .isRead(false)  // 추후 더 생각해볼것
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return savedMessage;
    }

    public ChatMessage getLatestMessage(ChatRoom room) {
        return chatMessageRepository.findTop1ByChatRoomOrderByIdDesc(room)
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    public Long getUnreadCount(ChatRoom room, Member member) {
        return chatMessageRepository.countByChatRoomAndMemberNotAndIsReadFalse(room, member);
    }

    // 읽음 여부 바꿈
    public void messageRead(Long roomId, Long memberId){
        ChatRoom chatRoom = chatRoomService.getChatRoom(roomId);
        Member member = authCommandService.findById(memberId);
        markAsRead(chatRoom, member);
    }

    // 읽음 여부 바꿈
    // 채팅방에 속해있으면서 memberNot에 reader(나)을 넣으면 내가 아닌 사람이 읽지않은 경우를 가져옴
    public void markAsRead(ChatRoom chatRoom, Member member) {
        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByChatRoomAndMemberNotAndIsReadFalse(chatRoom, member);
        for (ChatMessage chatMessage : unreadMessages) {
            chatMessage.markAsRead();
        }
    }
}
