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
import online.pictz.api.topic.exception.TopicSuggestBadRequest;
import online.pictz.api.topic.exception.TopicSuggestForbidden;
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

    /**
     * 선택지 이미지 연관관계 추가
     */
    public void addChoiceImage(TopicSuggestChoiceImage choiceImage) {
        choiceImages.add(choiceImage);
        choiceImage.setTopicSuggest(this);
    }

    /**
     * 선택지 이미지 연관관계 제거
     */
    public void removeChoiceImage(TopicSuggestChoiceImage choiceImage) {
        choiceImages.remove(choiceImage);
        choiceImage.setTopicSuggest(null);
    }

    /**
     * 문의 허용
     * @param updatedAt 업데이트 요청온 시간
     */
    public void approve(LocalDateTime updatedAt) {
        this.status = TopicSuggestStatus.APPROVED;
        this.updatedAt = updatedAt;
        this.rejectionReason = null;
    }

    /**
     * 문의 거부
     * @param rejectionReason 거부 이유
     * @param updatedAt 업데이트 요청온 시간
     */
    public void reject(String rejectionReason, LocalDateTime updatedAt) {
        this.status = TopicSuggestStatus.REJECTED;
        this.rejectionReason = rejectionReason;
        this.updatedAt = updatedAt;
    }

    /**
     * 문의 수정
     * @param title 문의 제목
     * @param updatedAt 업데이트 요청온 시간
     * @param description 문의 설명
     */
    public void updateDetails(String title, LocalDateTime updatedAt, String description) {
        this.title = title;
        this.updatedAt = updatedAt;
        this.description = description;
        this.status = TopicSuggestStatus.PENDING;
    }

    /**
     * 문의 썸네일 사진 업데이트
     * @param newThumbnailUrl 새로운 썸네일 이미지 URL
     */
    public void updateThumbnailUrl(String newThumbnailUrl) {
        this.thumbnailUrl = newThumbnailUrl;
    }

    /**
     * 요청자, 작성자 동일한지 검증
     * @param ownerId 작성자 ID
     * @param currentUserId 요청자 ID
     */
    public void validateSuggestOwner(Long ownerId, Long currentUserId) {
        if (!ownerId.equals(currentUserId)) {
            throw TopicSuggestForbidden.of(currentUserId, ownerId);
        }
    }

    /**
     * 문의 상태가 REJECTED(거부) 상태 아니면 예외 발생
     * @param status 요청온 문의 상태
     */
    public void validateRejected(TopicSuggestStatus status) {
        if (!status.equals(TopicSuggestStatus.REJECTED)) {
            throw TopicSuggestBadRequest.of(status);
        }
    }
}
