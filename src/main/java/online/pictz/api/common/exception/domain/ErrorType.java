package online.pictz.api.common.exception.domain;

import lombok.Getter;

/**
 * 에러 유형과 코드, 메시지 정의
 */
@Getter
public enum ErrorType {

    INTERNAL_SERVER_ERROR("E_500", "Internal server error"),

    BAD_REQUEST("E_400", "Bad request"),
    UNAUTHORIZED("E_401", "Unauthorized"),
    FORBIDDEN("E_403", "Forbidden"),
    PAGE_NOT_FOUND("E_404", "Page Not found"),
    CONFLICT("E_409", "Conflict"),
    RESOURCE_NOT_FOUND("E_404", "Resource Not Found"),
    TOO_MANY_REQUESTS("E_429", "Too Many Requests");

    private final String code;
    private final String message;

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

}