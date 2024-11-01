package online.pictz.api.user.infrastructure.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class NicknameClientException extends PictzException {

    public NicknameClientException(String message) {
        super(message);
    }

    public static NicknameClientException of(String message, String code) {
        return new NicknameClientException("Failed to request nickname from API: " + message + " (error code: " + code + ")");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getErrorCode() {
        return ErrorType.UNAUTHORIZED.getCode();
    }
}
