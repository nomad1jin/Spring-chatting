package practice.chatserver.member.dto;

import lombok.Builder;
import lombok.Getter;

public class AuthResponseDTO {

    @Getter
    @Builder
    public static class SignupResponseDTO {
        private Long id;
        private String username;
        private String nickname;
    }

    @Getter
    @Builder
    public static class LoginResponseDTO {
        private Long id;
        private String username;
        private String nickname;
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class ReissueResponseDTO {
        private Long id;
        private String accessToken;
    }
}
