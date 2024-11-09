package online.pictz.api.topic.service;

import java.util.List;
import online.pictz.api.topic.dto.TopicSuggestRequest;
import online.pictz.api.topic.dto.TopicSuggestResponse;

public interface TopicSuggestService {

    TopicSuggestResponse createSuggest(Long siteUserId, TopicSuggestRequest suggestRequest);

    List<TopicSuggestResponse> getUserTopicSuggestList(Long userId);

    TopicSuggestResponse getUserTopicSuggestDetail(Long suggestId, Long userId);

    TopicSuggestResponse updateTopicSuggest(Long topicSuggestId, Long userId,
        TopicSuggestRequest suggestRequest);

}
