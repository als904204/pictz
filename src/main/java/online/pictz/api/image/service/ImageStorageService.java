package online.pictz.api.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    /**
     * 이미지를 저장하고, 저장된 이미지의 URL을 반환
     *
     * @param file 저장할 이미지 파일
     * @return 저장된 이미지의 URL
     * @throws RuntimeException 이미지 저장 중 발생한 예외
     */
    String storeImage(MultipartFile file);

}
