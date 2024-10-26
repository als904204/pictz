package online.pictz.api.vote.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class VoteTooManyRequests extends PictzException {

    protected VoteTooManyRequests(String message) {
        super(message);
    }

    public static VoteTooManyRequests of(int voteCount) {
        return new VoteTooManyRequests("Too many vote requests : " + voteCount);
    }


    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }

    @Override
    public String getErrorCode() {
        return ErrorType.TOO_MANY_REQUESTS.getCode();
    }
}
