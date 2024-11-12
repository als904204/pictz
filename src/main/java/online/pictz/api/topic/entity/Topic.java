package online.pictz.api.topic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "total_vote_count", columnDefinition = "INT default 0")
    private int totalVoteCount;

    protected Topic() {
    }

    public void approve(LocalDateTime updatedAt,String thumbnailImageUrl) {
        this.status = TopicStatus.ACTIVE;
        this.updatedAt = updatedAt;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public void reject(LocalDateTime updatedAt) {
        this.status = TopicStatus.INACTIVE;
        this.updatedAt = updatedAt;
    }

    @Builder
    public Topic(Long suggestedTopicId, String title, String slug, TopicStatus status,
        String thumbnailImageUrl, LocalDateTime createdAt) {
        this.suggestedTopicId = suggestedTopicId;
        this.title = title;
        this.slug = slug;
        this.status = status;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.createdAt = createdAt;
    }
}