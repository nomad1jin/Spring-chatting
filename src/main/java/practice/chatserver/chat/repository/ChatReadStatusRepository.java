package practice.chatserver.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.chat.domain.ChatReadStatus;

import java.util.List;


@Repository
public interface ChatReadStatusRepository extends MongoRepository<ChatReadStatus, Long> {

    Long countByRoomIdAndMemberIdAndIsReadFalse(String roomId, Long memberId);
    List<ChatReadStatus> findByRoomIdAndMemberIdAndIsReadFalse(String roomId, Long memberId);

    // MongoDB의 update는 별도 서비스에서 처리
}
