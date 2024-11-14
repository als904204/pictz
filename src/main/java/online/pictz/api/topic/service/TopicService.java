package online.pictz.api.topic.service;

import java.util.List;
import online.pictz.api.common.dto.PagedResponse;
import online.pictz.api.topic.dto.TopicCountResponse;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;

public interface TopicService {

    PagedResponse<TopicResponse> getActiveTopics(TopicSort sortType, int page);

    List<TopicCountResponse> getAllTopicCounts(int page);

    PagedResponse<TopicResponse> searchTopics(String query, TopicSort sortBy, int page);
}
