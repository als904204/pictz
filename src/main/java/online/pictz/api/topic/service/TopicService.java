package online.pictz.api.topic.service;

import java.util.List;
import online.pictz.api.topic.dto.TopicResponse;

public interface TopicService {

    TopicResponse findBySlug(String slug);

    List<TopicResponse> findAll();

}
