package practice.chatserver.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import practice.chatserver.member.converter.AuthConverter;
import practice.chatserver.member.dto.AuthRequestDTO;
import practice.chatserver.member.dto.AuthResponseDTO;
import practice.chatserver.member.service.AuthCommandService;
import practice.chatserver.global.apiPayload.CustomResponse;
import practice.chatserver.global.apiPayload.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멍냥일지 Swagger API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;
    private final AuthConverter authConverter;

    @Operation(summary = "회원가입", description = "회원가입합니다.")
    @PostMapping("/signup")
    public CustomResponse<AuthResponseDTO.SignupResponseDTO> signup(@RequestBody AuthRequestDTO.SignupRequestDTO dto) {
        AuthResponseDTO.SignupResponseDTO signupResponseDTO = authCommandService.signUp(dto);
        return CustomResponse.onSuccess(SuccessCode.CREATED, signupResponseDTO);
    }

    @Operation(summary = "로그인", description = "로그인합니다.")
    @PostMapping("/login")
    public CustomResponse<AuthResponseDTO.LoginResponseDTO> login(@RequestBody AuthRequestDTO.LoginRequestDTO dto) {
        AuthResponseDTO.LoginResponseDTO loginResponseDTO = authCommandService.login(dto);
        return CustomResponse.onSuccess(SuccessCode.OK, loginResponseDTO);
    }

    @Operation(summary = "로그아웃", description = "로그아웃합니다.")
    @PostMapping("/logout")
    public CustomResponse<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authCommandService.logout(request, response);
        return CustomResponse.onSuccess(SuccessCode.OK);
    }

    @Operation(summary = "리이슈", description = "액세스 토큰을 재발행합니다. id와 refresh 필요")
    @PostMapping("/reissue")
    public CustomResponse<AuthResponseDTO.ReissueResponseDTO> reissue(@RequestBody AuthRequestDTO.ReissueRequestDTO dto) {
        AuthResponseDTO.ReissueResponseDTO reissue = authCommandService.reissue(dto);
        return CustomResponse.onSuccess(SuccessCode.OK, reissue);
    }

}
