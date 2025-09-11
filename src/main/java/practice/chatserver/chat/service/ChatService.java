package practice.chatserver.chat.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.repository.ChatMessageRepository;
import practice.chatserver.chat.repository.ChatParticipantRepository;
import practice.chatserver.chat.repository.ChatRoomRepository;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public ChatMessage saveMessage(ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO) {
        //채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageReqDTO.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOTFOUND));

        //보낸 사람 조회
        Member sender = memberRepository.findByUsername(chatMessageReqDTO.getSenderName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));

        //메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .chatMessage(chatMessageReqDTO.getMessage())
                .isRead(false)  // 추후 더 생각해볼것
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return savedMessage;
    }

    public void createRoom(String roomName) {
        //챗룸과 참여자
        Member member = memberRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .build();
        chatRoomRepository.save(chatRoom);

        // 채팅참여자로 개설자 추가
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }


//    public List<ChatResDTO.ChatRoomListResDTO> getGroupChatRooms() {
//        chatRoomRepository.findByIsGroupChat(String isGroupChat);
//    }
//
//    public List<ChatResDTO.ChatHistoryListResDTO> getChatHistory(Long roomId) {
//        // 참여자인지 검증
//        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
//                .orElseThrow(() -> new EntityNotFoundException("No Room"));
//        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
//                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
//
//        List<ChatParticipation> participations = chatParticipationRepository.findByChatRoom(chatRoom);
//        boolean check = false;
//        for (ChatParticipation participation : participations) {
//            if (participation.getMember().equals(member)) {
//                check = true;
//            }
//            else {
//                throw new IllegalArgumentException("Chat Participation not found");
//            }
//        }
//        // 특정 룸에 대한 메세지 조회
//        List<ChatMessage> historyMessages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
//        List<ChatResDTO.ChatHistoryListResDTO> chatHistoryList = new ArrayList<>();
//        for(ChatMessage chatMessage : historyMessages) {
//            ChatResDTO.ChatHistoryResDTO chatHistoryDto = ChatResDTO.ChatHistoryResDTO.builder()
//                    .
//        }


//    public void readMessage(Long roomId) {
//        ChatRoom chatRoom =  chatRoomRepository.findById(roomId)
//                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
//        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
//                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
//        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
//
//
//    }

    // 참여자인지 검증
    public boolean isRoomParticipant(String username, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOTFOUND));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (ChatParticipant chatParticipant : chatParticipants) {
            if (chatParticipant.getMember().equals(member)) {
                return true;
            }
        }
        return false;
    }

    // 로그인한 사람이 읽었는지 안 읽었는지
    public void messageRead(Long roomId, Long memberId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOTFOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));

        // 채팅방에 속해있으면서 memberNot에 reader(나)을 넣으면 내가 아닌 사람이 읽지않은 경우를 가져옴
        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByChatRoomAndMemberNotAndIsReadFalse(chatRoom, member);
        for (ChatMessage chatMessage : unreadMessages) {
            chatMessage.markAsRead();
        }
    }
}

