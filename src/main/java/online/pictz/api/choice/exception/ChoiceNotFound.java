package online.pictz.api.choice.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class ChoiceNotFound extends PictzException {

    private ChoiceNotFound(String message) {
        super(message);
    }

    public static ChoiceNotFound forChoiceId(Long choiceId) {
        return new ChoiceNotFound("No choice found with ID: " + choiceId);
    }

    public static ChoiceNotFound forTopicId(Long topicId) {
        return new ChoiceNotFound("No choices found for topic ID: " + topicId);
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