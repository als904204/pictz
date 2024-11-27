package online.pictz.api.topic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.user.entity.SiteUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TopicSuggestResponseConverterTest {

    private TopicSuggestResponseConverter topicSuggestResponseConverter;

    @BeforeEach
    void setUp() {
        topicSuggestResponseConverter = new TopicSuggestResponseConverter();
    }

    @Test
    void toResponse() {
        // given
        SiteUser user = new SiteUser("nickname", "foo");
        TopicSuggest topicSuggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .thumbnailUrl("url")
            .status(TopicSuggestStatus.PENDING)
            .build();

        // when
        TopicSuggestResponse response = topicSuggestResponseConverter.toResponse(topicSuggest);

        // then
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getContent()).isEqualTo("desc");
        assertThat(response.getStatus()).isEqualTo(TopicSuggestStatus.PENDING.getKorean());
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getThumbnailUrl()).isEqualTo("url");
        assertThat(response.getRejectReason()).isNull();
        assertThat(response.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 1, 1));

    }

    @Test
    void toResponseList() {
        // given
        SiteUser user = new SiteUser("nickname", "foo");

        List<TopicSuggest> suggests = List.of(
            TopicSuggest.builder()
                .title("title")
                .description("desc")
                .user(user)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
                .thumbnailUrl("url")
                .status(TopicSuggestStatus.PENDING)
                .build()
        );

        // when
        List<TopicSuggestResponse> responseList = topicSuggestResponseConverter.toResponseList(
            suggests);

        // then
        assertThat(responseList).hasSize(1);

        TopicSuggestResponse response = responseList.get(0);
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getContent()).isEqualTo("desc");
        assertThat(response.getStatus()).isEqualTo(TopicSuggestStatus.PENDING.getKorean());
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getThumbnailUrl()).isEqualTo("url");
        assertThat(response.getRejectReason()).isNull();
        assertThat(response.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 1, 1));
    }
}