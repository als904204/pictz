package online.pictz.api.topic.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import online.pictz.api.image.service.ImageStorageService;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.image.service.ImageStorageUtils;
import online.pictz.api.topic.dto.ChoiceImageRequest;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestUpdate;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.topic.exception.TopicSuggestDuplicate;
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
     * 토픽 문의 생성
     *
     * @param siteUserId     토픽 문의 유저 id
     * @param suggestRequest 토픽 문의 내용
     * @return 토픽 문의 응답
     */
    @Transactional
    @Override
    public TopicSuggestResponse createSuggest(Long siteUserId, TopicSuggestCreate suggestRequest) {

        SiteUser siteUser = getSiteUserById(siteUserId);
        String title = suggestRequest.getTitle();

        if (topicSuggestRepository.existsByTitle(title)) {
            throw TopicSuggestDuplicate.title(title);
        }

        // 토픽 썸네일 이미지 저장 후 URL 리턴
        String thumbnailUrl = imageStorageService.storeImage(suggestRequest.getThumbnail());

        // '토픽 문의' 엔티티 인스턴스 생성
        TopicSuggest suggest = TopicSuggest.builder()
            .title(title)
            .description(suggestRequest.getDescription())
            .user(siteUser)
            .thumbnailUrl(thumbnailUrl)
            .createdAt(timeProvider.getCurrentTime())
            .status(TopicSuggestStatus.PENDING)
            .build();

        for (MultipartFile file : suggestRequest.getChoiceImages()) {
            String choiceImageUrl = imageStorageService.storeImage(file);
            String choiceImageName = ImageStorageUtils.cleanFilename(file.getOriginalFilename());
            TopicSuggestChoiceImage choiceImage = new TopicSuggestChoiceImage(choiceImageUrl,
                choiceImageName);
            suggest.addChoiceImage(choiceImage);
        }

        topicSuggestRepository.save(suggest);

        return converter.toResponse(suggest);
    }


    /**
     * 로그인한 유저의 토픽 문의 목록 조회
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

    /**
     * 로그인한 유저의 토픽 문의 상세 정보 조회
     * @param suggestId 조회할 문의 id
     * @param userId    로그인 유저 id
     * @return 토픽 문의 상세 정보
     */
    @Transactional
    @Override
    public TopicSuggestResponse getUserTopicSuggestDetail(Long suggestId, Long userId) {
        SiteUser user = getSiteUserById(userId);
        TopicSuggest suggest = getSuggestById(suggestId);
        suggest.validateSuggestOwner(suggest.getUser().getId(), user.getId());
        return converter.toResponse(suggest);
    }

    /**
     * 토픽 문의 수정
     * @param topicSuggestId 수정할 토픽 문의 id
     * @param userId         로그인 유저 id
     * @param updateDto      수정 내용
     * @return 수정된 정보
     */
    @Override
    @Transactional
    public TopicSuggestResponse updateTopicSuggest(Long topicSuggestId, Long userId,
        TopicSuggestUpdate updateDto) {

        // 작성자 검증
        SiteUser user = getSiteUserById(userId);
        TopicSuggest suggest = getSuggestById(topicSuggestId);
        suggest.validateSuggestOwner(suggest.getUser().getId(), user.getId());

        // 거부 상태가 아닌데 요청 올 경우 검증
        suggest.validateRejected(suggest.getStatus());

        // 사진 필드를 제외한 나머지 필드 업데이트
        suggest.updateDetails(updateDto.getTitle(), timeProvider.getCurrentTime(),
            updateDto.getDescription());

        // 썸네일 사진 업로드 했을 경우
        if (updateDto.getThumbnail() != null && !updateDto.getThumbnail().isEmpty()) {
            imageStorageService.deleteImage(suggest.getThumbnailUrl());
            String newThumbnailUrl = imageStorageService.storeImage(updateDto.getThumbnail());
            suggest.updateThumbnailUrl(newThumbnailUrl);
        }

        // 선택지 사진 업로드 했을 경우
        List<ChoiceImageRequest> imageRequests = updateDto.getChoiceImages();
        if (imageRequests != null) {
            updateImageDetail(imageRequests, suggest);
        }

        // 업데이트된 suggest 저장
        topicSuggestRepository.save(suggest);

        return converter.toResponse(suggest);
    }

    private void updateImageDetail(List<ChoiceImageRequest> imageRequests, TopicSuggest suggest) {

        Map<Long, TopicSuggestChoiceImage> existingImgMap = TopicSuggestChoiceImage.getImageIdMap(suggest);

        for (ChoiceImageRequest imgRequest : imageRequests) {

            // 빈 객체가 올 경우 다음 배열 진행
            if (imgRequest == null) {
                continue;
            }

            MultipartFile newImgFile = imgRequest.getChoiceImage();

            // 해당 배열에 업로드한 이미지가 없을 경우 다음 배열 진행
            if (newImgFile == null || newImgFile.isEmpty()) {
                continue;
            }

            TopicSuggestChoiceImage existingImg = existingImgMap.get(imgRequest.getId());

            // 요청온 ID의 기존 이미지가 없다면 다음 배열 진행
            if (existingImg == null) {
                continue;
            }

            imageStorageService.deleteImage(existingImg.getImageUrl());
            String newImgUrl = imageStorageService.storeImage(newImgFile);
            String newFilename = ImageStorageUtils.cleanFilename(newImgUrl);

            existingImg.updateImageDetail(newImgUrl, newFilename);
        }
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
