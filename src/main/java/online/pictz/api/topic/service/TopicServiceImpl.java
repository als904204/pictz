package online.pictz.api.topic.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopicServiceImpl implements TopicService{

    private final TopicRepository topicRepository;

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
