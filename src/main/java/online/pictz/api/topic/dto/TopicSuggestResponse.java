package online.pictz.api.topic.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private String thumbnailUrl;
    private String rejectReason;
    private List<TopicSuggestChoiceImageResponse> choiceImages;

}
