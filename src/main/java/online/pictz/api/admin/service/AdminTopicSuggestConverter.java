package online.pictz.api.admin.service;

import java.util.stream.Collectors;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
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
            .createdAt(suggest.getCreatedAt())
            .choiceImageUrls(
                suggest.getChoiceImages().stream()
                    .map(TopicSuggestChoiceImage::getImageUrl)
                    .collect(Collectors.toList())
            )
            .build();
    }

}
