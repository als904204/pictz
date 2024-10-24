package online.pictz.api.topic.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.exception.ChoiceNotFound;
import online.pictz.api.choice.service.ChoiceService;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.service.TopicService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TopicApiControllerTest {


    @Mock
    private TopicService topicService;

    @Mock
    private ChoiceService choiceService;

    @InjectMocks
    private TopicApiController topicApiController;

    @DisplayName("slug로 topic을 성공적으로 조회한다")
    @Test
    void findBySlug_SUCCESS() {

        // given
        var slug = "slug";

        TopicResponse topicResponse = TopicResponse.builder()
            .id(1L)
            .suggestedTopicId(100L)
            .title("메호대전")
            .slug("ronaldo-vs-messi")
            .status(TopicStatus.ACTIVE)
            .thumbnailImageUrl("https://example.com/images/ronaldo-messi.jpg")
            .sharedCount(1500)
            .viewCount(50000)
            .createdAt(LocalDateTime.of(2024, 3, 15, 9, 0))
            .publishedAt(LocalDateTime.of(2024, 3, 15, 12, 0))
            .endAt(LocalDateTime.of(2024, 4, 15, 12, 0))
            .build();

        when(topicService.findBySlug(slug)).thenReturn(topicResponse);

        // when
        ResponseEntity<TopicResponse> apiResult = topicApiController.findBySlug(slug);

        // then
        assertThat(apiResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(apiResult.getBody()).getSlug()).isEqualTo("ronaldo-vs-messi");
        assertThat(Objects.requireNonNull(apiResult.getBody()).getTitle()).isEqualTo("메호대전");
        assertThat(Objects.requireNonNull(apiResult.getBody()).getStatus()).isEqualTo(TopicStatus.ACTIVE);
        assertThat(apiResult.getBody()).isEqualTo(topicResponse);
    }

    @DisplayName("slug에 해당하는 토픽이 존재하지 않을 때 예외가 발생한다")
    @Test
    void findBySlug_FAIL_whenTopicNotFound() {
        var slug = "hello";

        when(topicService.findBySlug(slug)).thenThrow(new TopicNotFound(slug));

        TopicNotFound exception = assertThrows(
            TopicNotFound.class,
            () -> topicApiController.findBySlug(slug)
        );

        assertThat(exception.getErrorCode()).isEqualTo("E_404");
        assertThat(exception.getMessage()).isEqualTo(String.format("Topic with slug '%s' not found", slug));
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}