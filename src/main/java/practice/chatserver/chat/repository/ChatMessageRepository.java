package practice.chatserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.member.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomAndMemberNotAndIsReadFalse(ChatRoom chatRoom, Member reader);
    Optional<ChatMessage> findTop1ByChatRoomOrderByIdDesc(ChatRoom chatRoom);
    Long countByChatRoomAndMemberNotAndIsReadFalse(ChatRoom room, Member member);

    List<ChatMessage> findByChatRoomIdOrderByCreatedTimeAsc(Long roomId);
}
