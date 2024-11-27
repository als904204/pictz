package online.pictz.api.common.exception.domain;

import lombok.Getter;

/**
 * API 응답 시 에러 정보
 */
@Getter
public class ApiErrorResponse {

    private final String code;
    private final String message;

    private ApiErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 실패 응답 생성
     * @param code     실패 코드
     * @param message  실패 메시지
     * @return ApiErrorResponse("E_404", "page not found")
     */
    public static ApiErrorResponse fail(String code, String message) {
        return new ApiErrorResponse(code, message);
    }

}
