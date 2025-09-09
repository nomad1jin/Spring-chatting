package practice.chatserver.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import practice.chatserver.member.entity.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;

    // 1. 파라미터에 yml에 있는 변수를 넣고 생성자 설정
    // 롬복의 value아니고 beans.factory.annotation
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.time.access-expiration}") long accessExpiration,
                   @Value("${jwt.time.refresh-expiration}") long refreshExpiration) {

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.refreshExpiration = Duration.ofMillis(refreshExpiration);
    }

    //2. creatToken() 만들기
    private String createToken(Member member, Duration expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(member.getUsername())  //Subject를 Username으로 설정
                .claim("id", member.getId())    //claim: 페이로드에 추가할 정보
                .claim("username", member.getUsername())
                .claim("type", member.getType())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(secretKey)
                .compact();
    }

    // 토큰을 복호화해서 페이로드 내용을 가져오는 역할
    public Jws<Claims> getClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token);
    }

    // 누구의 토큰인지 토큰에서 username만 꺼냄
    public String getUsername(String token) {
        try {
            return getClaims(token).getPayload().getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public Long getId(String token) {
        try {
            return getClaims(token).getPayload().get("id", Long.class);
        } catch (JwtException e) {
            return null;
        }
    }

    // 토큰이 유효한지 확인 - 나중에 filter에서 사용
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // 액세스 토큰 만료 여부
    public boolean isExpired(String token) {
        try {
            Date exp = getClaims(token).getPayload().getExpiration();
            return exp.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    //각 토큰 생성
    public String createAccessToken(Member member) {
        return createToken(member, accessExpiration);
    }
    public String createRefreshToken(Member member) {
        return createToken(member, refreshExpiration);
    }

}
