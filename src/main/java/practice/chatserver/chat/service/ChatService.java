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
    private final ChatParticipantService chatParticipantService;
    private final AuthCommandService authCommandService;

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

    @Transactional
    public ChatResDTO.ChatRoomCreatedDTO createRoom(Long initiatorId, Long targetId, String roomName) {
        // 사용자 검증
        Member initiator = authCommandService.findById(initiatorId);
        Member target = authCommandService.findById(targetId);

        // 중복방 체크하고 없으면 생성
        Optional<ChatRoom> existingRoom = chatRoomService.findExistingRoom(initiatorId, targetId);
        if(existingRoom.isPresent()) {
            ChatRoom chatRoom = existingRoom.get();
            return new ChatResDTO.ChatRoomCreatedDTO(chatRoom.getId(), chatRoom.getRoomName());
        }
        ChatRoom chatRoom = chatRoomService.makeChatRoom(roomName);

        // 두 참여자 모두 추가
        List<ChatParticipant> participants = Arrays.asList(
                ChatParticipant.builder().chatRoom(chatRoom).member(initiator).build(),
                ChatParticipant.builder().chatRoom(chatRoom).member(target).build()
        );
        chatParticipantRepository.saveAll(participants);

        return new ChatResDTO.ChatRoomCreatedDTO(chatRoom.getId(), chatRoom.getRoomName());
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
        Member member = authCommandService.findByUsername(username);
        return chatParticipantRepository.existsByChatRoomIdAndMemberId(roomId, member.getId());
    }

    // 로그인한 사람이 읽었는지 안 읽었는지
    public void messageRead(Long roomId, Long memberId){
        ChatRoom chatRoom = chatRoomService.getChatRoom(roomId);
        Member member = authCommandService.findById(memberId);

        // 채팅방에 속해있으면서 memberNot에 reader(나)을 넣으면 내가 아닌 사람이 읽지않은 경우를 가져옴
        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByChatRoomAndMemberNotAndIsReadFalse(chatRoom, member);
        for (ChatMessage chatMessage : unreadMessages) {
            chatMessage.markAsRead();
        }
    }
}

