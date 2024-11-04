package online.pictz.api.topic.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import online.pictz.api.topic.entity.TopicSuggest;

@AllArgsConstructor
@Getter
public class TopicSuggestResponse {

    private Long id;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nickname;

    public static TopicSuggestResponse from(TopicSuggest suggest) {
        return new TopicSuggestResponse(
            suggest.getId(),
            suggest.getTitle(),
            suggest.getDescription(),
            suggest.getStatus().getKorean(),
            suggest.getCreatedAt(),
            suggest.getUpdatedAt(),
            suggest.getUser().getNickname()
        );
    }
}
