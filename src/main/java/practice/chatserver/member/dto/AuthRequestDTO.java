package practice.chatserver.member.dto;

import lombok.Getter;
import practice.chatserver.member.enums.Gender;
import practice.chatserver.member.enums.Type;

public class AuthRequestDTO {

    @Getter
    public static class SignupRequestDTO {
        private String username;
        private String nickname;
        private Gender gender;
        private int old;
        private String address;
        private String phoneNumber;
        private Type type;
        private String loginId;
        private String password;
        private String password2;
        private String email;
    }

    @Getter
    public static class LoginRequestDTO {
        private String loginId;
        private String password;
    }

    @Getter
    public static class ReissueRequestDTO {
        private Long id;
        private String refreshToken;
    }
}
