package online.pictz.api.image.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import online.pictz.api.common.util.random.UuidHolder;
import online.pictz.api.image.exception.StorageException;
import online.pictz.api.mock.TestUuidHolder;
import online.pictz.api.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class LocalImageStorageServiceTest {

    private ImageStorageService imageStorageService;

    final String storagePath = "storagePath";
    final String basUrl = "basUrl";
    final String imagePath = "imagePath";
    final String fixedUUid = "abcd-1234-efgh-5678";
    UuidHolder uuidHolder;

    @BeforeEach
    void setUp() {

        uuidHolder = new TestUuidHolder(fixedUUid);

        imageStorageService = new LocalImageStorageService(storagePath, basUrl, imagePath, uuidHolder);
    }

    @Test
    void storeImage() {

        String fileType = ".jpeg";

        MultipartFile imageFile = TestUtils.createImageFile("fooFile", "fooName" + fileType,
            "image/jpeg",
            "helloFoo");

        String result = imageStorageService.storeImage(imageFile);

        assertThat(result).isEqualTo(basUrl + imagePath + uuidHolder.random() + fileType);
    }

    @DisplayName("잘못된 파일 확장자 이름 요청하면 예외 발생")
    @Test
    void storeImageFailWhenWrongFileExtension() {

        String fileType = ".....jpeg";

        MultipartFile imageFile = TestUtils.createImageFile("fooFile", "fooName" + fileType,
            "image/jpeg",
            "helloFoo");

        StorageException exception = assertThrows(StorageException.class,
            () -> imageStorageService.storeImage(imageFile));

        assertThat(exception.getMessage()).isEqualTo("Invalid file path: fooName.....jpeg");
    }

    @DisplayName("이미지가 아닌 파일 요청하면 예외 발생")
    @Test
    void storeImageFailWhenIstNotImageFile() {

        String fileType = ".exe";

        MultipartFile imageFile = TestUtils.createImageFile("fooFile", "fooName" + fileType,
            "image/jpeg",
            "helloFoo");

        StorageException exception = assertThrows(StorageException.class,
            () -> imageStorageService.storeImage(imageFile));

        assertThat(exception.getMessage()).isEqualTo("Only image files are allowed");
    }

    @Test
    void deleteImage() {

    }

    @Test
    void deleteImages() {
    }
}