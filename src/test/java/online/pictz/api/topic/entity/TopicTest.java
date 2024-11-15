package online.pictz.api.topic.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import online.pictz.api.mock.TestTimeProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TopicTest {

    private LocalDateTime fixedDateTime = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
    private LocalDateTime UpdatedDateTime = LocalDateTime.of(2024, 5, 1, 1, 1, 1);
    private TestTimeProvider fixedAt = new TestTimeProvider(fixedDateTime);
    private TestTimeProvider updatedAt = new TestTimeProvider(fixedDateTime);
    private Topic topic;

    @BeforeEach
    void setUp() {
        topic = Topic.builder()
            .suggestedTopicId(1L)
            .title("topic")
            .slug("slug")
            .status(TopicStatus.ACTIVE)
            .thumbnailImageUrl("url")
            .createdAt(fixedAt.getCurrentTime())
            .build();
    }

    @Test
    void approve() {

        // given
        final String newThumbnailUrl = "new url";

        // when
        topic.approve(updatedAt.getCurrentTime(), newThumbnailUrl);

        // then
        assertThat(topic.getUpdatedAt()).isEqualTo(updatedAt.getCurrentTime());
        assertThat(topic.getThumbnailImageUrl()).isEqualTo(newThumbnailUrl);
        assertThat(topic.getStatus()).isEqualTo(TopicStatus.ACTIVE);
    }

    @Test
    void reject() {
        // when
        topic.reject(updatedAt.getCurrentTime());

        // then
        assertThat(topic.getUpdatedAt()).isEqualTo(updatedAt.getCurrentTime());
        assertThat(topic.getStatus()).isEqualTo(TopicStatus.INACTIVE);
    }
}