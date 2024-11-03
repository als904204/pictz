package online.pictz.api.image.exception;

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


    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public String getErrorCode() {
        return "";
    }
}
