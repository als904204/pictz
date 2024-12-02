package online.pictz.api.image.service;

import static online.pictz.api.common.util.image.ImageStorageUtils.extractFilename;
import static online.pictz.api.common.util.image.ImageStorageUtils.getFileExtension;
import static online.pictz.api.common.util.image.ImageStorageUtils.isImageFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import online.pictz.api.common.util.random.UuidHolder;
import online.pictz.api.image.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Profile({"dev", "test"})
@Service
public class LocalImageStorageService implements ImageStorageService{

    private final Path storageSuffix;
    private final String baseUrl;
    private final String imagesPath;
    private final UuidHolder uuidHolder;

    public LocalImageStorageService(
        @Value("${storage.local.path}") String storagePath,
        @Value("${storage.local.base-url}") String baseUrl,
        @Value("${storage.local.images-path}") String imagesPath,
        UuidHolder uuidHolder) {

        this.storageSuffix = Paths.get(storagePath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        this.imagesPath = imagesPath;
        this.uuidHolder = uuidHolder;
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
            String uuidFileName = uuidHolder.random() + "." + fileExtension;

            Path targetLocation = storageSuffix.resolve(uuidFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return baseUrl + imagesPath + uuidFileName;
        } catch (IOException e) {
            throw StorageException.failedToStore();
        }

    }

    @Override
    public void deleteImage(String imageUrl) {
        String fileName = extractFilename(imageUrl, baseUrl, imagesPath);
        Path filePath = storageSuffix.resolve(fileName).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw StorageException.failedToDelete(fileName, e.getMessage());
        }
    }

    @Override
    public void deleteImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;
        imageUrls.forEach(this::deleteImage);
    }

}
