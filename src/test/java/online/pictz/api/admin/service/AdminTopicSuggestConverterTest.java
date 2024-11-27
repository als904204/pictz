package online.pictz.api.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.user.entity.SiteUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminTopicSuggestConverterTest {

    AdminTopicSuggestConverter converter;
    TimeProvider timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 1, 1));
    @BeforeEach
    void setUp() {
        converter = new AdminTopicSuggestConverter();
    }

    @Test
    void convertToResponse() {

        // given
        SiteUser user = new SiteUser("nickname", "foo");
        TopicSuggest topicSuggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(timeProvider.getCurrentTime())
            .thumbnailUrl("url")
            .status(TopicSuggestStatus.PENDING)
            .build();

        TopicSuggestChoiceImage image = new TopicSuggestChoiceImage("imgUrl", "imgName");

        topicSuggest.addChoiceImage(image);

        // when
        AdminTopicSuggestResponse result = converter.convertToResponse(
            topicSuggest);

        // then
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getDescription()).isEqualTo("desc");
        assertThat(result.getNickname()).isEqualTo("nickname");
        assertThat(result.getCreatedAt()).isEqualTo(timeProvider.getCurrentTime());
        assertThat(result.getThumbnailUrl()).isEqualTo("url");
        assertThat(result.getStatus()).isEqualTo(TopicSuggestStatus.PENDING.name());

        assertThat(result.getChoiceImages()).hasSize(1);
        assertThat(result.getChoiceImages().get(0).getImageUrl()).isEqualTo("imgUrl");
        assertThat(result.getChoiceImages().get(0).getFileName()).isEqualTo("imgName");
    }

}