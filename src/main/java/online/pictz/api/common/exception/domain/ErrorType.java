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
    NOT_FOUND("E_404", "Not found"),
    CONFLICT("E_409", "Conflict");

    private final String code;
    private final String message;

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

}