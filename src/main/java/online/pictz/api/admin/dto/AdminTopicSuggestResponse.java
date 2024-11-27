package online.pictz.api.admin.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import online.pictz.api.topic.dto.TopicSuggestChoiceImageResponse;

@Builder
@Getter
public class AdminTopicSuggestResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String nickname;
    private String status;
    private String rejectReason;
    private LocalDateTime createdAt;
    private List<TopicSuggestChoiceImageResponse> choiceImages;
}