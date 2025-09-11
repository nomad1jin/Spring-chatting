package practice.chatserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(Long roomId);
//    Optional<ChatRoom> findByInitiatorAndRoomId(Long initiatorId, Long roomId);

    @Query("""
    SELECT cr FROM ChatRoom cr 
    JOIN cr.participants p1 
    JOIN cr.participants p2 
    WHERE p1.member.id = :memberId1 
    AND p2.member.id = :memberId2 
    """)
    ChatRoom findExistingRoom(Long initiatorId, Long targetId);

    List<ChatRoom> findAllByMemberIdOrderByRecentMessage(Long memberId);

    List<ChatRoom> findRoomIdsByMemberId(Long memberId);
}


