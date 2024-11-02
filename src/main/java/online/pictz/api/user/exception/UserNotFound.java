package online.pictz.api.user.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class UserNotFound extends PictzException {

    public UserNotFound(String message) {
        super(message);
    }

    public static UserNotFound of(String providerId) {
        return new UserNotFound(String.format("User with providerId '%s' not found", providerId));
    }
    public static UserNotFound of(Long id) {
        return new UserNotFound(String.format("User with id '%s' not found", id));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorCode() {
        return ErrorType.RESOURCE_NOT_FOUND.getCode();
    }

}
