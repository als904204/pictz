package online.pictz.api.topic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.dto.TopicCreate;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.exception.TopicDuplicate;
import online.pictz.api.topic.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class TopicServiceImplTest {

    @Mock
    private TopicRepository topicRepository;

    private TopicServiceImpl topicService;

    private TimeProvider timeProvider;

    @BeforeEach
    void setUp() {
        timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        topicService = new TopicServiceImpl(topicRepository, timeProvider);
    }

    @DisplayName("slug로 topic을 성공적으로 조회한다")
    @Test
    void findBySlug() {

        // given
        Topic topic = Topic.builder()
            .suggestedTopicId(1L)
            .title("메시 vs 호날두")
            .slug("messi-vs-ronaldo")
            .status(TopicStatus.ACTIVE)
            .thumbnailImageUrl("http://example.com/image.jpg")
            .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
            .publishedAt(LocalDateTime.of(2024, 2, 1, 0, 0, 0))
            .endAt(LocalDateTime.of(2024, 3, 1, 0, 0, 0))
            .build();

        var slug = "messi-vs-ronaldo";
        when(topicRepository.findBySlug(slug)).thenReturn(Optional.of(topic));

        // when
        TopicResponse topicResponse = topicService.findBySlug(slug);

        // then
        assertThat(topicResponse.getSuggestedTopicId()).isEqualTo(1L);
        assertThat(topicResponse.getTitle()).isEqualTo("메시 vs 호날두");
        assertThat(topicResponse.getStatus()).isEqualTo(TopicStatus.ACTIVE);
        assertThat(topicResponse.getThumbnailImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(topicResponse.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        assertThat(topicResponse.getPublishedAt()).isEqualTo(LocalDateTime.of(2024, 2, 1, 0, 0, 0));
        assertThat(topicResponse.getEndAt()).isEqualTo(LocalDateTime.of(2024, 3, 1, 0, 0, 0));
    }

    @DisplayName("토픽 저장에 성공한다")
    @Test
    void createTopic_SUCCESS() {

        Topic topic = Topic.builder()
            .suggestedTopicId(1L)
            .title("메시 vs 호날두")
            .slug("messi-vs-ronaldo")
            .status(TopicStatus.ACTIVE)
            .thumbnailImageUrl("http://example.com/image.jpg")
            .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
            .publishedAt(LocalDateTime.of(2024, 2, 1, 0, 0, 0))
            .endAt(LocalDateTime.of(2024, 3, 1, 0, 0, 0))
            .build();

        when(topicRepository.save(any(Topic.class))).thenReturn(topic);

        TopicCreate topicCreateRequest = new TopicCreate(
            topic.getSuggestedTopicId(),
            topic.getTitle(),
            topic.getSlug(),
            topic.getStatus(),
            topic.getThumbnailImageUrl(),
            topic.getPublishedAt(),
            topic.getEndAt()
        );

        TopicResponse savedTopic = topicService.createTopic(topicCreateRequest);

        assertThat(savedTopic.getSuggestedTopicId()).isEqualTo(1L);
        assertThat(savedTopic.getTitle()).isEqualTo("메시 vs 호날두");
        assertThat(savedTopic.getSlug()).isEqualTo("messi-vs-ronaldo");
        assertThat(savedTopic.getStatus()).isEqualTo(TopicStatus.ACTIVE);
        assertThat(savedTopic.getThumbnailImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(savedTopic.getPublishedAt()).isEqualTo(LocalDateTime.of(2024, 2, 1, 0, 0, 0));
        assertThat(savedTopic.getEndAt()).isEqualTo(LocalDateTime.of(2024, 3, 1, 0, 0, 0));

    }

    @DisplayName("토픽 저장할 때 이미 저장된 title일경우 TopicDuplicate 발생")
    @Test
    void createTopic_FAIL_DuplicateTitle() {

        // given
        var duplicateTitle = "Duplicate Title";
        when(topicRepository.existsByTitle(duplicateTitle)).thenReturn(true);

        TopicCreate dupTopicCreateRequest = new TopicCreate(
            1L,
            duplicateTitle,
            "slug",
            TopicStatus.ACTIVE,
            "http://example.com/image.jpg",
            LocalDateTime.now(),
            null
        );

        // when
        TopicDuplicate exception = assertThrows(
            TopicDuplicate.class,
            () -> topicService.createTopic(dupTopicCreateRequest)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo("E_409");
        assertThat(exception.getMessage()).isEqualTo(
            "The topic title already exists: Duplicate Title");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @DisplayName("토픽 저장할 때 이미 저장된 slug일경우 TopicDuplicate 발생")
    @Test
    void createTopic_FAIL_DuplicateSlug() {

        // given
        var duplicateSlug = "Duplicate Slug";
        when(topicRepository.existsBySlug(duplicateSlug)).thenReturn(true);

        TopicCreate dupTopicCreateRequest = new TopicCreate(
            1L,
            "title",
            duplicateSlug,
            TopicStatus.ACTIVE,
            "http://example.com/image.jpg",
            LocalDateTime.now(),
            null
        );

        // when
        TopicDuplicate exception = assertThrows(
            TopicDuplicate.class,
            () -> topicService.createTopic(dupTopicCreateRequest)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo("E_409");
        assertThat(exception.getMessage()).isEqualTo("The topic slug already exists: Duplicate Slug");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

}