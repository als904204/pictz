package online.pictz.api.admin.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.repository.TopicSuggestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminTopicSuggestServiceImpl implements AdminTopicSuggestService{

    private final TopicSuggestRepository topicSuggestRepository;

    @Transactional(readOnly = true)
    @Override
    public List<AdminTopicSuggestResponse> getAllTopicSuggests() {

        List<TopicSuggest> suggests = topicSuggestRepository.findAll();

        return suggests.stream()
            .map(AdminTopicSuggestResponse::fromEntity)
            .collect(Collectors.toList());
    }

}
