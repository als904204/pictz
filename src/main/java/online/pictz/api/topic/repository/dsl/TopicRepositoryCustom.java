package online.pictz.api.topic.repository.dsl;

import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;
import org.springframework.data.domain.Page;

public interface TopicRepositoryCustom {
    Page<TopicResponse> findActiveTopics(TopicSort sortType, int page);
}
