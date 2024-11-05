package online.pictz.api.admin.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.repository.TopicSuggestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminTopicSuggestServiceImpl implements AdminTopicSuggestService{

    private final TopicSuggestRepository topicSuggestRepository;
    private final AdminTopicSuggestConverter converter;

    @Transactional(readOnly = true)
    @Override
    public List<AdminTopicSuggestResponse> getAllTopicSuggests() {
        return topicSuggestRepository.findAll().stream()
            .map(converter::convertToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public AdminTopicSuggestResponse getTopicById(Long id) {
        return topicSuggestRepository.findById(id)
            .map(converter::convertToResponse)
            .orElseThrow(() -> TopicNotFound.byId(id));
    }
}

