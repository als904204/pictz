package online.pictz.api.user.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class UserNotFound extends PictzException {

    public UserNotFound(String providerId) {
        super(String.format("User with providerId '%s' not found", providerId));
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
