package online.pictz.api.admin.service;

import java.util.stream.Collectors;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.topic.dto.TopicSuggestChoiceImageResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;
import org.springframework.stereotype.Component;

@Component
public class AdminTopicSuggestConverter {

    public AdminTopicSuggestResponse convertToResponse(TopicSuggest suggest) {
        return AdminTopicSuggestResponse.builder()
            .id(suggest.getId())
            .title(suggest.getTitle())
            .description(suggest.getDescription())
            .thumbnailUrl(suggest.getThumbnailUrl())
            .nickname(suggest.getUser().getNickname())
            .status(suggest.getStatus().name())
            .rejectReason(suggest.getRejectionReason())
            .createdAt(suggest.getCreatedAt())
            .choiceImages(
                suggest.getChoiceImages().stream()
                    .map(this::convertChoiceImageToResponse)
                    .collect(Collectors.toList())
            )
            .build();
    }

    private TopicSuggestChoiceImageResponse convertChoiceImageToResponse(TopicSuggestChoiceImage choiceImage) {
        return new TopicSuggestChoiceImageResponse(
            choiceImage.getId(),
            choiceImage.getImageUrl(),
            choiceImage.getFileName()
        );
    }

}
