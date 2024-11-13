package online.pictz.api.topic.service;

import java.util.List;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestRequest;
import online.pictz.api.topic.dto.TopicSuggestResponse;

public interface TopicSuggestService {

    TopicSuggestResponse createSuggest(Long siteUserId, TopicSuggestCreate createRequest);

    List<TopicSuggestResponse> getUserTopicSuggestList(Long userId);

    TopicSuggestResponse getUserTopicSuggestDetail(Long suggestId, Long userId);

    TopicSuggestResponse updateTopicSuggest(Long suggestId, Long userId,
        TopicSuggestRequest updateRequest);

}
