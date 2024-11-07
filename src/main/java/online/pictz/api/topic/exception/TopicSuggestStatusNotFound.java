package online.pictz.api.topic.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import org.springframework.http.HttpStatus;

public class TopicSuggestStatusNotFound extends PictzException {


    private TopicSuggestStatusNotFound(String message) {
        super(message);
    }

    public static TopicSuggestStatusNotFound byStatus(TopicSuggestStatus status) {
        return new TopicSuggestStatusNotFound("topicSuggestStatus not found with status : " + status);
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
