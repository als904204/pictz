package online.pictz.api.topic.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class TopicSuggestDuplicate extends PictzException {

    private TopicSuggestDuplicate(String message) {
        super(message);
    }

    public static TopicSuggestDuplicate title(String title) {
        return new TopicSuggestDuplicate("topicSuggest is already exists with title : " + title);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorCode() {
        return ErrorType.CONFLICT.getCode();
    }

}