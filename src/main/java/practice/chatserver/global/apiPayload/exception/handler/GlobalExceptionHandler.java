package practice.chatserver.global.apiPayload.exception.handler;

import practice.chatserver.global.apiPayload.CustomResponse;
import practice.chatserver.global.apiPayload.code.BaseCode;
import practice.chatserver.global.apiPayload.code.CustomException;
import practice.chatserver.global.apiPayload.code.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomResponse<?>> handleCustomException(CustomException ex) {
        log.warn("[ CustomException ]: {}", ex.getCode().getMessage());

        BaseCode errorCode = ex.getCode();

        // errorCode가 반환할 응답을 직접 생성하도록 변경
        CustomResponse<?> errorResponse = CustomResponse.onFailure(errorCode);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    // 컨트롤러 메서드에서 @Valid 어노테이션을 사용하여 DTO의 유효성 검사를 수행
    // @NotBlank, @Email 등이 붙어 있는데, 값이 잘못되면 이 예외 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CustomResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        BaseCode validationErrorCode = ErrorCode.VALIDATION_FAILED;

        CustomResponse<Map<String, String>> errorResponse = CustomResponse.onFailure(
                validationErrorCode.getHttpStatus(),
                validationErrorCode.getCode(),
                validationErrorCode.getMessage(),
                false,
                errors);

        return ResponseEntity.status(validationErrorCode.getHttpStatus()).body(errorResponse);
    }

    // 예상치 못한 일반 예외 처리
    @ExceptionHandler({Exception.class})
    public ResponseEntity<CustomResponse<String>> handleGeneralException(Exception ex) {
        log.error("[WARNING] Internal Server Error : {}", ex.getMessage());

        BaseCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR_500;

        // 코드/메시지 기반 실패 응답 생성 방식 통일
        CustomResponse<String> errorResponse = CustomResponse.onFailure(
                errorCode.getHttpStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                false,
                null
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }
}
