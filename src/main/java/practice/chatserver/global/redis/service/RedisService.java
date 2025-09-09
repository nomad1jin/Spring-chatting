package practice.chatserver.global.redis.service;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import practice.chatserver.global.jwt.JwtUtil;
import practice.chatserver.global.redis.entity.RedisRefreshToken;
import practice.chatserver.global.redis.repository.RedisRepository;

import java.time.Duration;
import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RedisService {

    private final JwtUtil jwtUtil;
    private final RedisRepository redisRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    @Transactional
    public void save(Long id, String refreshToken) {
        redisRepository.save(new RedisRefreshToken(id, refreshToken));
    }

    // 로그아웃할때 액세스토큰의 남은 만료시간을 저장해서 액세스토큰 사용못하게하기 (블랙리스트 등록)
    @Transactional
    public void addBlacklist(String accessToken) {
        Claims claims = jwtUtil.getClaims(accessToken).getPayload();
        long exp = claims.getExpiration().getTime();
        long now = new Date().getTime();
        long remaining = exp - now;

        if (remaining > 0) {
            // 액세스토큰을 키로 하고, 값은 남은 시간만큼
            String key = "blacklist:" + accessToken;
            redisTemplate.opsForValue().set(key, "true", Duration.ofMillis(remaining));
        }
    }

    // 로그아웃 시 refresh 토큰 삭제
    @Transactional
    public void deleteRefreshToken(Long id) {
        redisRepository.deleteById(id);
    }

    // 블랙리스트 검사, true면 블랙리스트에 존재한다.
    public boolean isBlacklisted(String accessToken) {
        String key = "blacklist:" + accessToken;
        if(redisTemplate.opsForValue().get(key) != null) {
            return true;
        } else {
            return false;
        }
    }

}