package online.pictz.api.topic.repository.dsl;

import java.util.List;
import online.pictz.api.topic.dto.TopicCountResponse;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;
import org.springframework.data.domain.Page;

public interface TopicRepositoryCustom {

    Page<TopicResponse> findActiveTopics(TopicSort sortType, int page);

    List<TopicCountResponse> getTopicTotalCounts(int page);
}
