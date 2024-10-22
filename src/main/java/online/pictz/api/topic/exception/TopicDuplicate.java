package online.pictz.api.topic.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class TopicDuplicate extends PictzException {

    public TopicDuplicate(String message) {
        super(message);
    }

    public static TopicDuplicate duplicateByTitle(String title) {
        return new TopicDuplicate("The topic title already exists: " + title);
    }

    public static TopicDuplicate duplicateBySlug(String slug) {
        return new TopicDuplicate("The topic slug already exists: " + slug);
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
