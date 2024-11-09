package online.pictz.api.topic.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.image.service.ImageStorageService;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.image.service.ImageStorageUtils;
import online.pictz.api.topic.dto.TopicSuggestRequest;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.topic.exception.TopicSuggestNotFound;
import online.pictz.api.topic.repository.TopicSuggestRepository;
import online.pictz.api.user.entity.SiteUser;
import online.pictz.api.user.exception.UserNotFound;
import online.pictz.api.user.repository.SiteUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class TopicSuggestServiceImpl implements TopicSuggestService{

    private final TopicSuggestRepository topicSuggestRepository;
    private final SiteUserRepository siteUserRepository;
    private final TimeProvider timeProvider;
    private final ImageStorageService imageStorageService;
    private final TopicSuggestResponseConverter converter;

    /**
     * 토픽 문의를 생성한다
     * @param siteUserId 토픽 문의 유저 id
     * @param suggestRequest 토픽 문의 내용
     * @return 토픽 문의 응답
     */
    @Transactional
    @Override
    public TopicSuggestResponse createSuggest(Long siteUserId, TopicSuggestRequest suggestRequest) {

        SiteUser siteUser = getSiteUserById(siteUserId);

        // 토픽 썸네일 이미지 저장 후 URL 리턴
        String thumbnailUrl = imageStorageService.storeImage(suggestRequest.getThumbnail());

        // '토픽 문의' 엔티티 인스턴스 생성
        TopicSuggest suggest = TopicSuggest.builder()
            .title(suggestRequest.getTitle())
            .description(suggestRequest.getDescription())
            .user(siteUser)
            .thumbnailUrl(thumbnailUrl)
            .createdAt(timeProvider.getCurrentTime())
            .status(TopicSuggestStatus.PENDING)
            .build();

        // '토픽 문의 선택지 이미지' 이미지 저장 후 '토픽 문의' 엔티티에 추가
        for (MultipartFile choiceImageFile : suggestRequest.getChoiceImages()) {
            String choiceImageUrl = imageStorageService.storeImage(choiceImageFile);
            String choiceImageName = ImageStorageUtils.cleanFilename(choiceImageFile.getOriginalFilename());
            TopicSuggestChoiceImage choiceImage = new TopicSuggestChoiceImage(choiceImageUrl,
                choiceImageName);
            suggest.addChoiceImage(choiceImage);
        }

        topicSuggestRepository.save(suggest);

        return converter.toResponse(suggest);
    }


    /**
     * 로그인한 유저의 토픽 문의 목록을 조회한다
     * @param siteUserId 로그인 유저 id
     * @return 해당 유저의 토픽 목록 리스트
     */
    @Transactional(readOnly = true)
    @Override
    public List<TopicSuggestResponse> getUserTopicSuggestList(Long siteUserId) {

        SiteUser siteUser = getSiteUserById(siteUserId);

        List<TopicSuggest> suggests = topicSuggestRepository.findByUserId(
            siteUser.getId());

        return converter.toResponseList(suggests);
    }

    @Transactional
    @Override
    public TopicSuggestResponse getUserTopicSuggestDetail(Long suggestId, Long userId) {
        SiteUser user = getSiteUserById(userId);
        TopicSuggest suggest = getSuggestById(suggestId);
        suggest.validateSuggestOwner(suggest.getUser().getId(), user.getId());

        return converter.toResponse(suggest);
    }

    /**
     *
     * @param topicSuggestId 수정할 토픽 문의
     * @param userId         요청한 사용자 id
     * @param updateDto      수정 내용
     * @return
     */
    @Transactional
    @Override
    public TopicSuggestResponse updateTopicSuggest(Long topicSuggestId, Long userId,
        TopicSuggestRequest updateDto) {

        // 1. 요청한 작성자가 수정하려는 문의 작성자인지 검증한다.
        SiteUser user = getSiteUserById(userId);
        TopicSuggest suggest = getSuggestById(topicSuggestId);

        suggest.validateSuggestOwner(suggest.getUser().getId(), user.getId());

        // 2. 제목과 설명을 업데이트 한다.
        suggest.updateDetails(updateDto.getTitle(), timeProvider.getCurrentTime(),
            updateDto.getDescription());

        // 3. 썸네일을 업데이트 한다.
        if (updateDto.getThumbnail() != null && !updateDto.getThumbnail().isEmpty()) {
            // 기존 썸네일 삭제
            imageStorageService.deleteImage(suggest.getThumbnailUrl());
            String newThumbnailUrl = imageStorageService.storeImage(updateDto.getThumbnail());
            suggest.updateThumbnailUrl(newThumbnailUrl);
        }

        // 4. 선택지 이미지를 업데이트 한다
        List<MultipartFile> choiceImageFiles = updateDto.getChoiceImages();
        if (choiceImageFiles != null && !choiceImageFiles.isEmpty()) {
            List<TopicSuggestChoiceImage> choiceImages = suggest.getChoiceImages();

            // 기존 선택지 이미지 삭제
            List<String> existsChoiceImagesUrl = choiceImages.stream()
                .map(TopicSuggestChoiceImage::getImageUrl)
                .collect(Collectors.toList());
            imageStorageService.deleteImages(existsChoiceImagesUrl);

            List<TopicSuggestChoiceImage> newChoiceImages = choiceImageFiles.stream()
                .map(file -> {
                    String imageUrl = imageStorageService.storeImage(file);
                    String imageName = ImageStorageUtils.cleanFilename(file.getOriginalFilename());
                    return new TopicSuggestChoiceImage(imageUrl, imageName);
                })
                .collect(Collectors.toList());

            suggest.updateChoiceImages(newChoiceImages);
        }

        // 5. 업데이트된 suggest 저장
        topicSuggestRepository.save(suggest);

        return converter.toResponse(suggest);
    }

    private TopicSuggest getSuggestById(Long suggestId) {
        return topicSuggestRepository.findById(suggestId)
            .orElseThrow(() -> TopicSuggestNotFound.byId(suggestId));
    }

    private SiteUser getSiteUserById(Long siteUserId) {
        return siteUserRepository.findById(siteUserId)
            .orElseThrow(() -> UserNotFound.of(siteUserId));
    }
}
