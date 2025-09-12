package practice.chatserver.chat.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.chatserver.chat.dto.ChatReqDTO;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.dto.ChatResDTO;
import practice.chatserver.chat.repository.ChatMessageRepository;
import practice.chatserver.chat.repository.ChatParticipantRepository;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.service.AuthCommandService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    private final AuthCommandService authCommandService;


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

}

