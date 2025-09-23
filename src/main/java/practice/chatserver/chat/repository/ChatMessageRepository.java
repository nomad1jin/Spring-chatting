package practice.chatserver.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatMessage;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, Long> {
    Optional<ChatMessage> findFirstByRoomIdOrderByCreatedTimeDesc(String roomId);
    List<ChatMessage> findByRoomIdOrderByCreatedTimeAsc(String roomId);
}
