package online.pictz.api.image.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import online.pictz.api.image.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Profile("dev")
@Service
public class LocalImageStorageService implements ImageStorageService{

    private final Path storageSuffix;
    private final String baseUrl;

    public LocalImageStorageService(
        @Value("${storage.local.path}") String storagePath,
        @Value("${storage.local.base-url}") String baseUrl) {
        this.storageSuffix = Paths.get(storagePath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        try {
            Files.createDirectories(this.storageSuffix);
        } catch (IOException e) {
            throw StorageException.directoryCreationFailed(e);
        }
    }

    @Override
    public String storeImage(MultipartFile file){

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try{
            if (fileName.contains("..")) {
                throw StorageException.invalidFilePath(fileName);
            }

            if (!isImageFile(fileName)) {
                throw StorageException.notImageFile();
            }

            String fileExtension = getFileExtension(fileName);
            String uuidFileName = UUID.randomUUID() + "." + fileExtension;

            Path targetLocation = storageSuffix.resolve(uuidFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return baseUrl + "/images/" + uuidFileName;
        } catch (IOException e) {
            throw StorageException.failedToStore();
        }

    }

    /**
     * 이미지 파일인지 검증
     *
     * @param filename 파일 이름
     * @return 이미지 파일 여부
     */
    private boolean isImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") ||
            extension.equals("png") || extension.equals("bmp");
    }

    /**
     * 파일의 확장자 반환
     *
     * @param filename 파일 이름
     * @return 파일 확장자
     */
    private String getFileExtension(String filename) {
        return StringUtils.getFilenameExtension(filename);
    }
}
