package online.pictz.api.topic.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicStatus;

@Getter
@Builder
public class TopicResponse {

    private Long id;
    private Long suggestedTopicId;
    private String title;
    private String slug;
    private TopicStatus status;
    private String thumbnailImageUrl;
    private int sharedCount;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime endAt;

    /**
     * @param topic 변환할 Topic 엔티티
     * @return 생성된 TopicResponse 객체
     */
    public static TopicResponse from(Topic topic) {
        return TopicResponse.builder()
            .id(topic.getId())
            .suggestedTopicId(topic.getSuggestedTopicId())
            .title(topic.getTitle())
            .slug(topic.getSlug())
            .status(topic.getStatus())
            .thumbnailImageUrl(topic.getThumbnailImageUrl())
            .createdAt(topic.getCreatedAt())
            .build();
    }
}
