package online.pictz.api.admin.service;

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
    public AdminTopicSuggestResponse getSuggestById(Long id) {
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
                topicSuggest.approve(timeProvider.getCurrentTime());
                topicRepository.findBySuggestedTopicId(suggestId).ifPresentOrElse(
                    approveTopic -> updateTopic(approveTopic, topicSuggest), // 존재하는 토픽이라면 업데이트
                    () -> saveNewTopic(suggestId, topicSuggest)); // 없다면 저장
                return new AdminTopicSuggestUpdateResponse(status, "topic is approved", null);

            case REJECTED:
                topicSuggest.reject(rejectReason, timeProvider.getCurrentTime());
                topicRepository.findBySuggestedTopicId(suggestId).ifPresent(
                    rejectTopic -> rejectTopic.reject(timeProvider.getCurrentTime())
                );
                return new AdminTopicSuggestUpdateResponse(status, "topic is rejected", rejectReason);

            default:
                throw TopicSuggestStatusNotFound.byStatus(status);
        }
    }

    private void updateTopic(Topic topic, TopicSuggest suggest) {
        topic.approve(timeProvider.getCurrentTime(), suggest.getThumbnailUrl());
        List<TopicSuggestChoiceImage> images = suggest.getChoiceImages();
        List<Choice> choices = choiceRepository.findByTopicId(topic.getId());
        Choice.updateFrom(choices, images);
        choiceRepository.saveAll(choices);
    }


    private void saveNewTopic(Long suggestId, TopicSuggest suggest) {

        String slug = slugGenerator.generate();

        Topic newTopic = Topic.builder()
            .suggestedTopicId(suggestId)
            .title(suggest.getTitle())
            .slug(slug)
            .thumbnailImageUrl(suggest.getThumbnailUrl())
            .status(TopicStatus.ACTIVE)
            .createdAt(timeProvider.getCurrentTime())
            .build();

        Long topicId = topicRepository.save(newTopic).getId();

        List<TopicSuggestChoiceImage> choiceImages = suggest.getChoiceImages();
        List<Choice> newChoices = Choice.createFrom(topicId, choiceImages);
        choiceRepository.saveAll(newChoices);
    }

}

