package practice.chatserver.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.repository.ChatParticipantRepository;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.service.AuthCommandService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final AuthCommandService authCommandService;
    
    public void saveAllParticipants(List<ChatParticipant> chatParticipants) {
        chatParticipantRepository.saveAll(chatParticipants);
    }

    // 참여자인지 검증
    public boolean isRoomParticipant(String username, Long roomId) {
        Member member = authCommandService.findByUsername(username);
        return chatParticipantRepository.existsByChatRoomIdAndMemberId(roomId, member.getId());
    }

    // 참여자 2명 중에서 나를 제외한 한명 (상대방)
    public Member getTargetMember(ChatRoom room, Member loginMember) {
        return room.getParticipants().stream()
                .map(ChatParticipant::getMember)
                .filter(member -> !member.getId().equals(loginMember.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));
    }

    public ChatParticipant findByMemberId(Long memberId) {
        ChatParticipant participant = chatParticipantRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOTFOUND));
        return participant;
    }


}
