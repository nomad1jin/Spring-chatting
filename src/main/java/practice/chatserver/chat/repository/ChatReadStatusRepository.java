package practice.chatserver.chat.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatReadStatus;
import practice.chatserver.chat.domain.ChatRoom;
import practice.chatserver.member.entity.Member;


@Repository
public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {


    Long countByChatRoomAndMemberAndIsReadFalse(ChatRoom chatRoom, Member member);

    @Modifying
    @Transactional
    @Query("UPDATE ChatReadStatus crs " +
            "SET crs.isRead = true " +
            "WHERE crs.chatRoom.id = :roomId " +
            "AND crs.member.id = :memberId " +
            "AND crs.isRead = false")
    int markMessagesAsRead(@Param("roomId") Long roomId, @Param("memberId") Long memberId);
}
