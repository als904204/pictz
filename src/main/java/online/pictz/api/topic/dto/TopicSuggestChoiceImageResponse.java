package online.pictz.api.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopicSuggestChoiceImageResponse {
    private Long id;
    private String imageUrl;
    private String fileName;
}
