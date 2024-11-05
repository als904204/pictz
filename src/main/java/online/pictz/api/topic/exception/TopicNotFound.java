package online.pictz.api.topic.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class TopicNotFound extends PictzException {

    private TopicNotFound(String message) {
        super(message);
    }

    public static TopicNotFound byId(Long id) {
        return new TopicNotFound("topic not found with id : " + id);
    }

    public static TopicNotFound bySlug(String slug) {
        return new TopicNotFound("Topic with slug not found : " + slug);
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