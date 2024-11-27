package online.pictz.api.admin.service;

import java.util.List;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.admin.dto.AdminTopicSuggestUpdateResponse;
import online.pictz.api.topic.entity.TopicSuggestStatus;

public interface AdminTopicSuggestService {

    List<AdminTopicSuggestResponse> getAllTopicSuggests();

    AdminTopicSuggestResponse getSuggestById(Long id);

    AdminTopicSuggestUpdateResponse patchTopicSuggestStatus(Long id, TopicSuggestStatus status, String reason);
}
