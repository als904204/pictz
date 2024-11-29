package online.pictz.api.image.exception;

import java.io.IOException;
import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class S3StorageException extends PictzException {

    private S3StorageException(String message) {
        super(message);
    }

    private S3StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public static S3StorageException uploadFailed(IOException e) {
        return new S3StorageException("Failed to upload file to S3: " + e.getMessage(), e);
    }

    public static S3StorageException deleteFailed(String fileName, String message) {
        return new S3StorageException(
            "Failed to delete file in S3: " + fileName + ", error : " + message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getErrorCode() {
        return ErrorType.INTERNAL_SERVER_ERROR.getCode();
    }
}
