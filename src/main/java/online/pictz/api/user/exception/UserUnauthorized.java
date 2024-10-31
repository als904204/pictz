package online.pictz.api.user.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class UserUnauthorized extends PictzException {

    public UserUnauthorized(String message) {
        super(message);
    }

    public static UserUnauthorized of() {
        return new UserUnauthorized("User Unauthorized access");
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
