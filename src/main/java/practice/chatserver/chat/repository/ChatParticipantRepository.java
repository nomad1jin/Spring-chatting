package practice.chatserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatParticipant;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.member.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatRoomId(Long roomId);
    List<ChatParticipant> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);
    Optional<ChatParticipant> findByMemberIdAndChatRoomId(Long memberId, Long roomId);
    boolean existsByChatRoomIdAndMemberId(Long roomId, Long memberId);
}

