package practice.chatserver.chat.service;

import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.chatserver.global.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class ChatEntryService {

    private final JwtUtil jwtUtil;

}
