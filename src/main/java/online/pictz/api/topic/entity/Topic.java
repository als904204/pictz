package online.pictz.api.topic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "suggested_topic_id")
    private Long suggestedTopicId;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TopicStatus status;

    @Column(name = "thumbnail_image_url", nullable = false)
    private String thumbnailImageUrl;

    @Column(name = "shared_count", nullable = false, columnDefinition = "INT default 0")
    private int sharedCount;

    @Column(name = "view_count", nullable = false, columnDefinition = "INT default 0")
    private int viewCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    private LocalDateTime endAt;

    protected Topic() {
    }

    @Builder
    public Topic(Long suggestedTopicId, String title, String slug, TopicStatus status,
        String thumbnailImageUrl, LocalDateTime createdAt,
        LocalDateTime publishedAt,
        LocalDateTime endAt) {
        this.suggestedTopicId = suggestedTopicId;
        this.title = title;
        this.slug = slug;
        this.status = status;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.sharedCount = 0;
        this.viewCount = 0;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        this.endAt = endAt;
    }
}