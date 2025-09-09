package practice.chatserver.global.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import practice.chatserver.global.redis.entity.RedisRefreshToken;

@Repository
public interface RedisRepository extends CrudRepository<RedisRefreshToken, Long> {
}
