package online.pictz.api.topic.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class TopicSuggestForbidden extends PictzException {

    private TopicSuggestForbidden(String message) {
        super(message);
    }

    public static TopicSuggestForbidden of(Long currentUserId, Long ownerId) {
        return new TopicSuggestForbidden("User with ID " + currentUserId + " is not authorized to modify TopicSuggest created by user with ID " + ownerId);
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