package practice.chatserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.member.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends MongoRepository<ChatParticipant, String> {
    List<ChatParticipant> findByRoomId(String roomId);
    Optional<ChatParticipant> findByMemberIdAndRoomId(Long memberId, String roomId);
    boolean existsByRoomIdAndMemberId(String roomId, Long memberId);
}

