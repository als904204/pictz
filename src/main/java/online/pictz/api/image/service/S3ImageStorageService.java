package online.pictz.api.image.service;

import static online.pictz.api.common.util.image.ImageStorageUtils.getFileExtension;
import static online.pictz.api.common.util.image.ImageStorageUtils.isImageFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.common.util.random.UuidHolder;
import online.pictz.api.image.exception.S3StorageException;
import online.pictz.api.image.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Profile("prod")
@Service
public class S3ImageStorageService implements ImageStorageService{

    private final AmazonS3 s3Client;
    private final UuidHolder uuidHolder;

    @Value("${s3.bucket}")
    private String bucket;

    @Override
    public String storeImage(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName.contains("..")) {
            throw StorageException.invalidFilePath(originalFileName);
        }

        if (!isImageFile(originalFileName)) {
            throw StorageException.notImageFile();
        }

        String fileExtension = getFileExtension(originalFileName);
        String newFileName = uuidHolder.random() + "." + fileExtension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            s3Client.putObject(bucket, newFileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw S3StorageException.uploadFailed(e);
        }

        return s3Client.getUrl(bucket, newFileName).toString();
    }

    @Override
    public void deleteImage(String imageUrl) {

    }

    @Override
    public void deleteImages(List<String> imageUrls) {

    }
}
