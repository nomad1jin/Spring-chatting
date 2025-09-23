package practice.chatserver.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.chat.repository.ChatParticipantRepository;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import practice.chatserver.member.entity.Member;
import practice.chatserver.member.service.AuthCommandService;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final AuthCommandService authCommandService;
    private final MongoTemplate mongoTemplate;
    
    public void saveAllParticipants(List<ChatParticipant> chatParticipants) {
        chatParticipantRepository.saveAll(chatParticipants);
    }

    // 참여자인지 검증
    public boolean isRoomParticipant(String username, String roomId) {
        Member member = authCommandService.findByUsername(username);
        return chatParticipantRepository.existsByRoomIdAndMemberId(roomId, member.getId());
    }

    // 참여자 2명 중에서 나를 제외한 한명 (상대방)
    public Member getTargetMember(ChatRoom room, Member loginMember) {
        Query query = new Query(Criteria.where("roomId").is(room.getId())
                .and("memberId").ne(loginMember.getId()));  //ne
        ChatParticipant targetParticipant = mongoTemplate.findOne(query, ChatParticipant.class);

        if (targetParticipant == null) {
            throw new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        return authCommandService.findById(targetParticipant.getMemberId());
    }

    public ChatParticipant findByMemberIdAndRoomId(Long memberId, String roomId) {
        return chatParticipantRepository.findByMemberIdAndRoomId(memberId, roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND));
    }

    public List<ChatParticipant> findByChatRoomId(String roomId) {
        return chatParticipantRepository.findByRoomId(roomId);
    }
}
