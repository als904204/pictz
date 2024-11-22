package online.pictz.api.image.service;

import java.util.Objects;
import online.pictz.api.image.exception.StorageException;
import org.springframework.util.StringUtils;

public class ImageStorageUtils {

    private ImageStorageUtils(){}

    /**
     * 이미지 파일인지 검증
     * @param filename 파일 이름
     * @return 이미지 파일 여부
     */
    public static boolean isImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") ||
            extension.equals("png") || extension.equals("bmp") || extension.equals("webp");
    }

    /**
     * 파일의 확장자 반환
     * @param filename 파일 이름
     * @return 파일 확장자
     */
    public static String getFileExtension(String filename) {
        return StringUtils.getFilenameExtension(filename);
    }

    /**
     * 공백, 확장자 제거
     * @param fileName 파일 이름
     * @return hello world.jpg -> helloworld
     */
    public static String cleanFilename(String fileName) {
        return Objects.requireNonNull(fileName)
            .replace(" ", "")
            .replaceAll("\\.[^.]+$", "");
    }

    /**
     * 이미지 URL에서 파일 이름 추출
     *
     * @param imageUrl "https://example.com/images/hello.jpg"
     * @param baseUrl 베이스 URL
     * @param imagesPath 이미지 경로
     * @return "hello.jpg"
     */
    public static String extractFilename(String imageUrl, String baseUrl, String imagesPath) {
        int startIndex = baseUrl.length() + imagesPath.length();

        if (imageUrl.length() <= startIndex) {
            throw StorageException.invalidFilePath(imageUrl);
        }
        return imageUrl.substring(startIndex);
    }
}

