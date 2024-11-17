package online.pictz.api.topic.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.exception.TopicSuggestBadRequest;
import online.pictz.api.topic.exception.TopicSuggestForbidden;
import online.pictz.api.user.entity.SiteUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TopicSuggestTest {

    private TopicSuggest topicSuggest;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        TimeProvider timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 1, 1));
        SiteUser user = new SiteUser("nickname", "foo");
        topicSuggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(timeProvider.getCurrentTime())
            .thumbnailUrl("url")
            .status(TopicSuggestStatus.PENDING)
            .build();

        updatedAt = LocalDateTime.of(2999, 1, 1, 1, 1);
    }

    @DisplayName("토픽 문의와 선택지 이미지 객체 연관관계가 설정되어야 한다")
    @Test
    void addChoiceImage() {
        // given
        TopicSuggestChoiceImage expectedImage = new TopicSuggestChoiceImage("imageUrl", "foo.jpg");

        // when
        topicSuggest.addChoiceImage(expectedImage);

        // then
        TopicSuggestChoiceImage actualImage = topicSuggest.getChoiceImages().get(0);
        assertThat(expectedImage).isEqualTo(actualImage);
        assertThat(expectedImage.getImageUrl()).isEqualTo("imageUrl");
        assertThat(expectedImage.getFileName()).isEqualTo("foo.jpg");
    }

    @DisplayName("토픽 문의와 선택지 이미지 객체 연관관계가 삭제되어야 한다")
    @Test
    void removeChoiceImage() {

        // given
        TopicSuggestChoiceImage expectedImage = new TopicSuggestChoiceImage("imageUrl", "foo.jpg");
        topicSuggest.addChoiceImage(expectedImage);

        // when
        topicSuggest.removeChoiceImage(expectedImage);

        // then
        assertThat(topicSuggest.getChoiceImages().isEmpty()).isTrue();
    }

    @DisplayName("문의 허용 시, APPROVED 상태가 되어야 한다")
    @Test
    void approve() {
        // given & when
        topicSuggest.approve(updatedAt);

        // then
        assertThat(topicSuggest.getStatus()).isEqualTo(TopicSuggestStatus.APPROVED);
        assertThat(topicSuggest.getRejectionReason()).isNull();
        assertThat(topicSuggest.getUpdatedAt()).isNotNull();
        assertThat(topicSuggest.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @DisplayName("문의 거부 시, REJECETD 상태가 되어야 한다")
    @Test
    void reject() {
        // given
        String reason = "이름 잘못됨";

        // when
        topicSuggest.reject(reason, updatedAt);

        // then
        assertThat(topicSuggest.getStatus()).isEqualTo(TopicSuggestStatus.REJECTED);
        assertThat(topicSuggest.getRejectionReason()).isEqualTo("이름 잘못됨");
        assertThat(topicSuggest.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @DisplayName("문의 상세정보 수정시, 제목, 수정일, 설명이 변경되어야 한다")
    @Test
    void updateDetail() {
        // given
        String title = "update title";
        String description = "update description";

        // when
        topicSuggest.updateDetails(title, updatedAt, description);

        // then
        assertThat(topicSuggest.getTitle()).isEqualTo(title);
        assertThat(topicSuggest.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(topicSuggest.getDescription()).isEqualTo(description);
    }

    @DisplayName("문의 썸네일 URL 업데이트")
    @Test
    void updateThumbnailUrl() {
        // given
        String newUrl = "new url";

        // when
        topicSuggest.updateThumbnailUrl(newUrl);

        // then
        assertThat(topicSuggest.getThumbnailUrl()).isEqualTo("new url");
    }

    @DisplayName("요청자와 작성자가 다르면 예외가 발생해야 한다")
    @Test
    void should_throw_when_ownerAndRequesterDifferent() {
        // given
        Long ownerId = 1L;
        Long currentUserId = 999L;

        // when
        TopicSuggestForbidden exception = assertThrows(TopicSuggestForbidden.class,
            () -> topicSuggest.validateSuggestOwner(ownerId, currentUserId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo("E_403");
        assertThat(exception.getMessage()).isEqualTo(
            "User with ID 999 is not authorized to modify TopicSuggest created by user with ID 1");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @DisplayName("문의 상태가 REJECT가 아니면 예외가 발생해야 한다")
    @Test
    void should_throw_when_statusIsNotRJECECT() {

        // when
        TopicSuggestBadRequest exception = assertThrows(TopicSuggestBadRequest.class,
            () -> topicSuggest.validateRejected(TopicSuggestStatus.PENDING));

        assertThat(exception.getMessage()).isEqualTo("The suggest is not REJECTED status : 보류중");
        assertThat(exception.getErrorCode()).isEqualTo("E_403");

    }
}