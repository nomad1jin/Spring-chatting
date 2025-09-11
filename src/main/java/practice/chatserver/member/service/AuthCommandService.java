package practice.chatserver.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import practice.chatserver.member.dto.AuthRequestDTO;
import practice.chatserver.member.dto.AuthResponseDTO;
import practice.chatserver.member.entity.Member;


public interface AuthCommandService {
    AuthResponseDTO.SignupResponseDTO signUp(AuthRequestDTO.SignupRequestDTO dto);
    AuthResponseDTO.LoginResponseDTO login(AuthRequestDTO.LoginRequestDTO dto);
    void logout(HttpServletRequest request,  HttpServletResponse response);
    AuthResponseDTO.ReissueResponseDTO reissue(AuthRequestDTO.ReissueRequestDTO dto);
    Member findById(Long memberId);
}
