package practice.chatserver.chat.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // findById는 기본 제공 (String ID로 자동 변경)
    Optional<ChatRoom> findById(String id);
}


