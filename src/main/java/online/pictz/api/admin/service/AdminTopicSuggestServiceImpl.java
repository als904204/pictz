package online.pictz.api.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.admin.dto.AdminTopicSuggestUpdateResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.exception.TopicSuggestStatusNotFound;
import online.pictz.api.topic.repository.TopicRepository;
import online.pictz.api.topic.repository.TopicSuggestRepository;
import online.pictz.api.topic.service.SlugGenerator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Service
public class AdminTopicSuggestServiceImpl implements AdminTopicSuggestService{

    private final TopicSuggestRepository topicSuggestRepository;
    private final TopicRepository topicRepository;
    private final ChoiceRepository choiceRepository;
    private final AdminTopicSuggestConverter converter;
    private final TimeProvider timeProvider;
    private final SlugGenerator slugGenerator;

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

    @Override
    @Transactional
    public AdminTopicSuggestUpdateResponse patchTopicSuggestStatus(Long suggestId, TopicSuggestStatus status,
        String rejectReason) {

        TopicSuggest topicSuggest = topicSuggestRepository.findById(suggestId)
            .orElseThrow(() -> TopicNotFound.bySuggestedTopicId(suggestId));

        switch (status) {
            case APPROVED:
                topicSuggest.approve(status, timeProvider.getCurrentTime());
                topicRepository.findBySuggestedTopicId(suggestId).ifPresentOrElse(
                    approveTopic-> approveTopic.changeStatus(TopicStatus.ACTIVE, timeProvider.getCurrentTime()), // 있는 토픽이라면 상태만 '활성화'
                    () -> saveNewTopic(suggestId, topicSuggest)      // 없는 토픽이라면 DB 저장
                );
                return new AdminTopicSuggestUpdateResponse(status, "topic is approved", null);
            case REJECTED:
                Topic rejectTopic = topicRepository.findBySuggestedTopicId(suggestId)
                    .orElseThrow(() -> TopicNotFound.bySuggestedTopicId(suggestId));
                rejectTopic.changeStatus(TopicStatus.INACTIVE, timeProvider.getCurrentTime());
                topicSuggest.reject(status, rejectReason, timeProvider.getCurrentTime());
                return new AdminTopicSuggestUpdateResponse(status, "topic is rejected", rejectReason);
            default:
                throw TopicSuggestStatusNotFound.byStatus(status);
        }
    }

    private void saveNewTopic(Long suggestId, TopicSuggest topicSuggest) {

        String slug = slugGenerator.generate();

        Topic newTopic = Topic.builder()
            .suggestedTopicId(suggestId)
            .title(topicSuggest.getTitle())
            .slug(slug)
            .thumbnailImageUrl(topicSuggest.getThumbnailUrl())
            .status(TopicStatus.ACTIVE)
            .createdAt(timeProvider.getCurrentTime())
            .build();

        Long topicId = topicRepository.save(newTopic).getId();

        List<TopicSuggestChoiceImage> choiceImages = topicSuggest.getChoiceImages();
        List<Choice> choices = new ArrayList<>();
        for (TopicSuggestChoiceImage choiceImage : choiceImages) {
            choices.add(new Choice(topicId, choiceImage.getFileName(), choiceImage.getImageUrl()));
        }
        choiceRepository.saveAll(choices);
    }

}

