package online.pictz.api.common.exception.domain;

import org.springframework.http.HttpStatus;

/**
 * 모든 커스텀 예외의 기본 클래스이며, 예외 HTTP 상태, 에러 코드, 로깅 필요 여부를 정의
 */
public abstract class PictzException extends RuntimeException {

    protected PictzException(String message) {
        super(message);
    }

    protected PictzException(String message, Throwable cause) {
        super(message, cause);
    }

    protected PictzException(Throwable cause) {
        super(cause);
    }

    protected PictzException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public abstract HttpStatus getHttpStatus();

    public abstract String getErrorCode();

    public abstract boolean isNecessaryToLog();

}