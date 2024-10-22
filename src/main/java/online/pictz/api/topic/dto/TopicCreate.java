package online.pictz.api.topic.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import online.pictz.api.topic.entity.TopicStatus;

@Getter
public class TopicCreate {

    private Long suggestedTopicId;
    private String title;
    private String slug;
    private TopicStatus status;
    private String thumbnailImageUrl;
    private LocalDateTime publishedAt;
    private LocalDateTime endAt;

}
