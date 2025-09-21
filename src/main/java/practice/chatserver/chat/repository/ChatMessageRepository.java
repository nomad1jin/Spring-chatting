package practice.chatserver.chat.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatMessage;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.member.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatMessage> findTop1ByChatRoomOrderByIdDesc(ChatRoom chatRoom);
    List<ChatMessage> findByChatRoomIdOrderByCreatedTimeAsc(Long roomId);

//    @Modifying
//    @Transactional
//    @Query("UPDATE ChatMessage cm " +
//            "SET cm.isRead = true " +
//            "WHERE cm.chatRoom.id = :roomId " +
//            "AND cm.member.id = :memberId " +
//            "AND cm.isRead = false")
//    int markMessagesAsRead(@Param("roomId") Long roomId, @Param("memberId") Long memberId);
}
