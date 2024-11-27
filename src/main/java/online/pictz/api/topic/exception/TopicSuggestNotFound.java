package online.pictz.api.topic.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class TopicSuggestNotFound extends PictzException {

    private TopicSuggestNotFound(String message) {
        super(message);
    }

    public static TopicSuggestNotFound byId(Long id) {
        return new TopicSuggestNotFound("topicSuggest not found with id : " + id);
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