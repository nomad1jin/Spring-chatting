package practice.chatserver.chat.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatParticipation;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.dto.ChatResDTO;
import practice.chatserver.chat.repository.ChatMessageRepository;
import practice.chatserver.chat.repository.ChatParticipationRepository;
import practice.chatserver.chat.repository.ChatRoomRepository;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatParticipationRepository chatParticipationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public void sendMessage(Long roomId, ChatReqDTO.ChatMessageReqDTO chatMessageReqDTO) {
        //채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("No Room"));

        //보낸 사람 조회
        Member sender = memberRepository.findByUsername(chatMessageReqDTO.getSenderName())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        //복합키 participation 조회
        ChatParticipation chatParticipation = chatParticipationRepository.findByMemberAndChatRoom(sender, chatRoom)
                .orElseThrow(() -> new EntityNotFoundException("Chat Participation not found"));

        //메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .participation(chatParticipation)   //participation에서 꺼내야함을 잊지 말 것...
                .message(chatMessageReqDTO.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);

//        //사용자별로 읽음 여부 저장
//        List<ChatParticipation> chatParticipationList = chatParticipationRepository.findByMemberAndChatRoom(sender, chatRoom);
//        for()

    }

    public void createGroupRoom(String roomName) {
        //챗룸과 참여자
        Member member = memberRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Username not found"));

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .build();
        chatRoomRepository.save(chatRoom);

        // 채팅참여자로 개설자 추가
        ChatParticipation chatParticipation = ChatParticipation.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
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


    public void readMessage(Long roomId) {
        ChatRoom chatRoom =  chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);


    }

}

