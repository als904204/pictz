package online.pictz.api.topic.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicStatus;

@Getter
@Builder
@AllArgsConstructor
public class TopicResponse {

    private Long id;
    private Long suggestedTopicId;
    private String title;
    private String slug;
    private TopicStatus status;
    private String thumbnailImageUrl;
    private LocalDateTime createdAt;

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
