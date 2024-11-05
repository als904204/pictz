package online.pictz.api.admin.service;

import java.util.List;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;

public interface AdminTopicSuggestService {

    List<AdminTopicSuggestResponse> getAllTopicSuggests();

    AdminTopicSuggestResponse getTopicById(Long id);
}
