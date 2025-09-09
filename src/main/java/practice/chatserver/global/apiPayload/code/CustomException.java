package practice.chatserver.global.apiPayload.code;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final BaseCode code;

    public CustomException(BaseCode baseCode) {
        super(baseCode.getMessage());
        this.code = baseCode;
    }
}
