package online.pictz.api.topic.service;

import java.util.List;
import java.util.stream.Collectors;
import online.pictz.api.topic.dto.TopicSuggestChoiceImageResponse;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import org.springframework.stereotype.Component;

@Component
public class TopicSuggestResponseConverter {

    public TopicSuggestResponse toResponse(TopicSuggest suggest) {
        List<TopicSuggestChoiceImageResponse> choiceImages = suggest.getChoiceImages().stream()
            .map(choiceImage -> new TopicSuggestChoiceImageResponse(choiceImage.getImageUrl(),
                choiceImage.getFileName()))
            .collect(Collectors.toList());

        return new TopicSuggestResponse(
            suggest.getId(),
            suggest.getTitle(),
            suggest.getDescription(),
            suggest.getStatus().getKorean(),
            suggest.getCreatedAt(),
            suggest.getUpdatedAt(),
            suggest.getUser().getNickname(),
            suggest.getThumbnailUrl(),
            suggest.getRejectionReason(),
            choiceImages
        );
    }

    public List<TopicSuggestResponse> toResponseList(List<TopicSuggest> suggests) {
        return suggests.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
