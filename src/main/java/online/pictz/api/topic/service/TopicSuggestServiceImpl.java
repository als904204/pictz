package online.pictz.api.topic.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.image.service.ImageStorageService;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;
import online.pictz.api.topic.entity.TopicSuggestStatus;
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

    /**
     * 토픽 문의를 생성한다
     * @param siteUserId 토픽 문의 유저 id
     * @param suggestRequest 토픽 문의 내용
     * @return 토픽 문의 응답
     */
    @Transactional
    @Override
    public TopicSuggestResponse createSuggest(Long siteUserId, TopicSuggestCreate suggestRequest) {

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
            String choiceImageName = cleanFilename(choiceImageFile.getOriginalFilename());
            TopicSuggestChoiceImage choiceImage = new TopicSuggestChoiceImage(choiceImageUrl,
                choiceImageName);
            suggest.addChoiceImage(choiceImage);
        }

        topicSuggestRepository.save(suggest);

        return TopicSuggestResponse.from(suggest);
    }


    /**
     * 로그인한 유저의 토픽 문의 목록을 조회한다
     * @param siteUserId 로그인 유저 id
     * @return 해당 유저의 토픽 목록 리스트
     */
    @Transactional(readOnly = true)
    @Override
    public List<TopicSuggestResponse> getTopicSuggestListByUserId(Long siteUserId) {

        SiteUser siteUser = getSiteUserById(siteUserId);

        List<TopicSuggest> userTopicSuggestList = topicSuggestRepository.findByUserId(
            siteUser.getId());

        return userTopicSuggestList.stream()
            .map(TopicSuggestResponse::from)
            .collect(Collectors.toList());
    }

    private SiteUser getSiteUserById(Long siteUserId) {
        return siteUserRepository.findById(siteUserId)
            .orElseThrow(() -> UserNotFound.of(siteUserId));
    }

    /**
     * 공백, 확장자 제거
     * @param fileName 파일 이름
     * @return hello world.jpg -> helloworld
     */
    private String cleanFilename(String fileName) {
        return Objects.requireNonNull(fileName)
            .replace(" ", "")
            .replaceAll("\\.[^.]+$", "");
    }


}
