package practice.chatserver.global.redis.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "token", timeToLive = 86400)
public class RedisRefreshToken {

    @Id @NonNull
    private Long id;
    private String refreshToken;
}
