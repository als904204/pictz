package online.pictz.api.common.exception.handler;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import online.pictz.api.common.exception.domain.PictzException;
import online.pictz.api.common.exception.domain.ApiErrorResponse;
import online.pictz.api.common.exception.domain.ErrorType;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 전역에서 발생하는 글로벌 예외처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PictzException.class)
    protected ResponseEntity<ApiErrorResponse> handleBusinessException(PictzException exception) {
        return ResponseEntity
            .status(exception.getHttpStatus())
            .body(ApiErrorResponse.fail(exception.getErrorCode(), exception.getMessage()));
    }

    @ExceptionHandler({
        IllegalArgumentException.class,                // 메서드에 전달된 잘못된 인자
        MissingServletRequestParameterException.class, // 필수 요청 파라미터가 누락(쿼리파라미터, 폼데이터)
        MethodArgumentTypeMismatchException.class,     // 파라미터의 타입이 메서드의 파라미터 타입과 일치하지 않을 때
        HttpMessageNotReadableException.class,         // 요청 본문을 파싱할 수 없거나 잘못된 형식의 데이터가 요청 본문에 포함되어 있을 때
        HttpMediaTypeNotSupportedException.class,      // 요청한 미디어 타입(Content-Type)이 서버가 지원하지 않을 때
        HttpMediaTypeNotAcceptableException.class,     // 서버가 해당 미디어 타입으로 응답을 생성할 수 없을 때
        BindException.class,                           // 폼 객체를 모델 객체에 바인딩 할 때(폼 데이터의 바인딩 과정에서 유효성 검사 실패나 타입 불일치 등)
        InvalidDataAccessApiUsageException.class       // @Valid 를 사용하지 않은 채 body 값 누락되었을 때
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiErrorResponse handleBadRequest(Exception exception) {
        log.warn("[BadRequest] Message={}", exception.getMessage());
        return ApiErrorResponse.fail(ErrorType.BAD_REQUEST.getCode(), ErrorType.BAD_REQUEST.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class) // 없는 페이지 요청
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ApiErrorResponse handleResourceNotFound(Exception exception) {
        log.warn("[PageNotFound] Message={}", exception.getMessage());
        return ApiErrorResponse.fail(ErrorType.PAGE_NOT_FOUND.getCode(), ErrorType.PAGE_NOT_FOUND.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // 유효성 검사 예외 별도 핸들링
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("[ValidationFailed] Message={}", errorMessage);
        return ApiErrorResponse.fail(ErrorType.BAD_REQUEST.getCode(), errorMessage);
    }

    @ExceptionHandler(Exception.class) // 그 외 에러 (관리자에게 메세지로 알려줘야 함)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiErrorResponse handleInternalServerError(Exception exception) {
        log.error("[Internal Server Error!!!] Message={}", exception.getMessage(), exception);
        return ApiErrorResponse.fail(ErrorType.INTERNAL_SERVER_ERROR.getCode(), ErrorType.INTERNAL_SERVER_ERROR.getMessage());
    }

}
