package online.pictz.api.topic.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
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

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicSuggestStatus status;

    /**
     * cascade = CascadeType.ALL: TopicSuggest 엔티티에 대한 모든 영속성 작업(저장, 삭제 등)이 choiceImages에도 적용됨
     * orphanRemoval = true: choiceImages 리스트에서 제거된 TopicSuggestChoiceImage 엔티티는 데이터베이스에서도 삭제됨
     * JsonManagedReference = DTO를 활용하여 순환 참조 방지
     */

    @OneToMany(mappedBy = "topicSuggest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TopicSuggestChoiceImage> choiceImages = new ArrayList<>();

    protected TopicSuggest() {}

    @Builder
    public TopicSuggest(
        String title, String description, SiteUser user, LocalDateTime createdAt,
        String thumbnailUrl, TopicSuggestStatus status) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.createdAt = createdAt;
        this.status = status;
        this.thumbnailUrl = thumbnailUrl;
    }

    // 연관관계 편의 메서드
    public void addChoiceImage(TopicSuggestChoiceImage choiceImage) {
        choiceImages.add(choiceImage);
        choiceImage.setTopicSuggest(this);
    }

    public void removeChoiceImage(TopicSuggestChoiceImage choiceImage) {
        choiceImages.remove(choiceImage);
        choiceImage.setTopicSuggest(null);
    }


    public void approve(LocalDateTime updatedAt) {
        this.status = TopicSuggestStatus.APPROVED;
        this.updatedAt = updatedAt;
        this.rejectionReason = null;
    }

    public void reject(String rejectionReason, LocalDateTime currentTime) {
        this.status = TopicSuggestStatus.REJECTED;
        this.rejectionReason = rejectionReason;
        this.updatedAt = currentTime;
    }
}
