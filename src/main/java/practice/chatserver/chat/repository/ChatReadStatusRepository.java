package practice.chatserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatReadStatus;

@Repository
public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {
}
