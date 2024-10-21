package online.pictz.api.choice.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class ChoiceNotFound extends PictzException {

    public ChoiceNotFound(Long id) {
        super(String.format("Choice with id '%d' not found", id));
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