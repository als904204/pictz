package online.pictz.api.topic.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import org.springframework.http.HttpStatus;

public class TopicSuggestBadRequest extends PictzException {

    private TopicSuggestBadRequest(String message) {
        super(message);
    }

    public static TopicSuggestBadRequest of(TopicSuggestStatus status) {
        return new TopicSuggestBadRequest("The suggest is not REJECTED status : " + status.getKorean());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getErrorCode() {
        return ErrorType.FORBIDDEN.getCode();
    }

}