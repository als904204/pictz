package online.pictz.api.image.exception;

import online.pictz.api.common.exception.domain.ErrorType;
import online.pictz.api.common.exception.domain.PictzException;
import org.springframework.http.HttpStatus;

public class StorageException extends PictzException {

    protected StorageException(String message) {
        super(message);
    }

    public static StorageException directoryCreationFailed(Exception e) {
        return new StorageException("Failed to create directory: " + e.getMessage());
    }

    public static StorageException invalidFilePath(String fileName) {
        return new StorageException("Invalid file path: " + fileName);
    }

    public static StorageException notImageFile() {
        return new StorageException("Only image files are allowed");
    }

    public static StorageException failedToStore() {
        return new StorageException("Failed to store image locally");
    }

    public static StorageException failedToDelete(String filename, String cause) {
        return new StorageException("Failed to delete file: " + filename + ", cause : " + cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorCode() {
        return ErrorType.BAD_REQUEST.getCode();
    }
}
