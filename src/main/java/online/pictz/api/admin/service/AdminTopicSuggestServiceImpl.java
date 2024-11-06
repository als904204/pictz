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

    @Transactional
    @Override
    public AdminTopicSuggestUpdateResponse patchTopicSuggestStatus(Long id, TopicSuggestStatus status,
        String rejectReason) {
        TopicSuggest topicSuggest = topicSuggestRepository.findById(id)
            .orElseThrow(() -> TopicNotFound.byId(id));

        topicSuggest.updateStatus(status, timeProvider.getCurrentTime());
        Topic topic;

        if (status.equals(TopicSuggestStatus.APPROVED)) {
            String slug = slugGenerator.generate();

            topic = Topic.builder()
                .suggestedTopicId(id)
                .title(topicSuggest.getTitle())
                .slug(slug)
                .thumbnailImageUrl(topicSuggest.getThumbnailUrl())
                .status(TopicStatus.ACTIVE)
                .createdAt(timeProvider.getCurrentTime())
                .build();

            Long topicId = topicRepository.save(topic).getId();

            List<TopicSuggestChoiceImage> choiceImages = topicSuggest.getChoiceImages();

            List<Choice> choices = new ArrayList<>();
            for (TopicSuggestChoiceImage choiceImage : choiceImages) {
                choices.add(new Choice(topicId, choiceImage.getFileName(), choiceImage.getImageUrl()));
            }

            choiceRepository.saveAll(choices);
            return new AdminTopicSuggestUpdateResponse(status, "topic is approved", null);

        } else {
            topicSuggest.updateRejectReason(rejectReason);
            return new AdminTopicSuggestUpdateResponse(status, "topic is rejected", rejectReason);
        }


    }
}

