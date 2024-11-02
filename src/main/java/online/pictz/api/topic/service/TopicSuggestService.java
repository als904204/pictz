package online.pictz.api.topic.service;

import java.util.List;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestResponse;

public interface TopicSuggestService {

    TopicSuggestResponse createSuggest(Long siteUserId, TopicSuggestCreate suggestRequest);

    List<TopicSuggestResponse> getTopicSuggestListByUserId(Long siteUserId);

}
