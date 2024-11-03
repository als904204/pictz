package online.pictz.api.topic.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import online.pictz.api.user.entity.SiteUser;

@Entity
@Table(name = "topic_suggests")
@Getter
public class TopicSuggest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicSuggestStatus status;

    protected TopicSuggest() {}

    @Builder
    public TopicSuggest(String title, String description, SiteUser user, LocalDateTime createdAt,
        String thumbnailUrl, TopicSuggestStatus status) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.createdAt = createdAt;
        this.status = status;
        this.thumbnailUrl = thumbnailUrl;
    }
}
