package online.pictz.api.topic.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.image.service.ImageStorageService;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.topic.repository.TopicSuggestRepository;
import online.pictz.api.user.entity.SiteUser;
import online.pictz.api.user.exception.UserNotFound;
import online.pictz.api.user.repository.SiteUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TopicSuggestServiceImpl implements TopicSuggestService{

    private final TopicSuggestRepository topicSuggestRepository;
    private final SiteUserRepository siteUserRepository;
    private final TimeProvider timeProvider;
    private final ImageStorageService imageStorageService;

    @Transactional
    @Override
    public TopicSuggestResponse createSuggest(Long siteUserId, TopicSuggestCreate suggestRequest) {

        SiteUser siteUser = siteUserRepository.findById(siteUserId)
            .orElseThrow(() -> UserNotFound.of(siteUserId));

        String thumbnailUrl = imageStorageService.storeImage(suggestRequest.getThumbnail());

        TopicSuggest suggest = TopicSuggest.builder()
            .title(suggestRequest.getTitle())
            .description(suggestRequest.getDescription())
            .user(siteUser)
            .thumbnailUrl(thumbnailUrl)
            .createdAt(timeProvider.getCurrentTime())
            .status(TopicSuggestStatus.PENDING)
            .build();

        topicSuggestRepository.save(suggest);

        return new TopicSuggestResponse(
            suggest.getId(),
            suggest.getTitle(),
            suggest.getDescription(),
            suggest.getStatus().getKorean(),
            suggest.getCreatedAt(),
            suggest.getUpdatedAt(),
            siteUser.getNickname()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<TopicSuggestResponse> getTopicSuggestListByUserId(Long siteUserId) {

        SiteUser siteUser = siteUserRepository.findById(siteUserId)
            .orElseThrow(() -> UserNotFound.of(siteUserId));

        List<TopicSuggest> userTopicSuggestList = topicSuggestRepository.findByUserId(
            siteUser.getId());

        return userTopicSuggestList.stream()
            .map(topic -> new TopicSuggestResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getDescription(),
                topic.getStatus().name(),
                topic.getCreatedAt(),
                topic.getUpdatedAt(),
                siteUser.getNickname()
            )) .collect(Collectors.toList());
    }
}
