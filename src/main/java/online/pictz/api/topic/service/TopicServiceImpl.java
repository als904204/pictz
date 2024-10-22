package online.pictz.api.topic.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.topic.dto.TopicCreate;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.exception.TopicDuplicate;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopicServiceImpl implements TopicService{

    private final TopicRepository topicRepository;
    private final TimeProvider timeProvider;

    /**
     * 요청받은 토픽 정보를 토대로 관리자가 생성한다.
     * @param topicCreate 생성할 토픽 DTO
     * @return 새로 생성된 토픽
     */
    @Override
    public TopicResponse createTopic(TopicCreate topicCreate) {

        if (topicRepository.existsBySlug(topicCreate.getSlug())) {
            throw TopicDuplicate.duplicateBySlug(topicCreate.getSlug());
        }

        if (topicRepository.existsByTitle(topicCreate.getTitle())) {
            throw TopicDuplicate.duplicateByTitle(topicCreate.getTitle());
        }

        Topic newTopic = Topic.builder()
            .suggestedTopicId(topicCreate.getSuggestedTopicId())
            .title(topicCreate.getTitle())
            .slug(topicCreate.getSlug())
            .status(topicCreate.getStatus())
            .thumbnailImageUrl(topicCreate.getThumbnailImageUrl())
            .publishedAt(topicCreate.getPublishedAt())
            .createdAt(timeProvider.getCurrentTime())
            .endAt(topicCreate.getEndAt())
            .build();

        Topic savedTopic = topicRepository.save(newTopic);

        return TopicResponse.from(savedTopic);
    }

    /**
     * slug로 토픽 조회
     * @param slug 조회할 slug
     * @return 조회된 TopicResponse
     * @throws TopicNotFound slug에 해당하는 토픽이 존재하지 않을 경우
     */
    @Override
    public TopicResponse findBySlug(String slug) {
        log.info("Finding topic with slug: {}", slug);
        Topic topic = topicRepository.findBySlug(slug).orElseThrow(() -> new TopicNotFound(slug));
        return TopicResponse.from(topic);
    }

    /**
     * 모든 토픽 조회
     * TODO: 추후 페이징으로 변경
     * @return 토픽 목록
     */
    @Override
    public List<TopicResponse> findAll() {
        return topicRepository.findAll()
            .stream()
            .map(TopicResponse::from)
            .toList();
    }

}
