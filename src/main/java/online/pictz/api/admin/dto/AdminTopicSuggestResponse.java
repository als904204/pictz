package online.pictz.api.admin.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import online.pictz.api.topic.entity.TopicSuggest;

@Getter
public class AdminTopicSuggestResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String nickname;
    private String status;
    private LocalDateTime createdAt;

    public AdminTopicSuggestResponse(Long id, String title, String nickname, String thumbnailUrl,
        String description, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.nickname = nickname;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static AdminTopicSuggestResponse fromEntity(TopicSuggest suggest) {
        return new AdminTopicSuggestResponse(
            suggest.getId(),
            suggest.getTitle(),
            suggest.getUser().getNickname(),
            suggest.getThumbnailUrl(),
            suggest.getDescription(),
            suggest.getStatus().name(),
            suggest.getCreatedAt()
        );
    }
}