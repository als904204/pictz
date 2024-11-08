package online.pictz.api.topic.service;

import java.util.List;
import online.pictz.api.common.dto.PagedResponse;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;

public interface TopicService {

    List<TopicResponse> findAll();

    PagedResponse<TopicResponse> getActiveTopics(TopicSort sortType, int page);

}
