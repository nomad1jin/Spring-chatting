package practice.chatserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatParticipation;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.member.entity.Member;

import java.util.Optional;

@Repository
public interface ChatParticipationRepository extends JpaRepository<ChatParticipation, Long> {
    Optional<ChatParticipation> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);
}
