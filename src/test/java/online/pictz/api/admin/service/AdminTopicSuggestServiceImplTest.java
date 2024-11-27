package online.pictz.api.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.admin.dto.AdminTopicSuggestUpdateResponse;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestSlugGenerator;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.topic.repository.TopicRepository;
import online.pictz.api.topic.repository.TopicSuggestRepository;
import online.pictz.api.user.entity.SiteUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AdminTopicSuggestServiceImplTest {

    private AdminTopicSuggestServiceImpl adminTopicSuggestService;

    @Mock
    private TopicSuggestRepository topicSuggestRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ChoiceRepository choiceRepository;

    TimeProvider timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 1, 1));


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        AdminTopicSuggestConverter converter = new AdminTopicSuggestConverter();
        SlugGenerator slugGenerator = new TestSlugGenerator("slug");

        adminTopicSuggestService = new AdminTopicSuggestServiceImpl(
            topicSuggestRepository,
            topicRepository,
            choiceRepository,
            converter,
            timeProvider,
            slugGenerator
        );
    }

    @Test
    void getAllTopicSuggests() {
        // given
        SiteUser user = new SiteUser("nickname", "foo");

        List<TopicSuggest> suggests = List.of(
             TopicSuggest.builder()
                .title("title")
                .description("desc")
                .user(user)
                .createdAt(timeProvider.getCurrentTime())
                .thumbnailUrl("url")
                .status(TopicSuggestStatus.PENDING)
                .build()
        );

        when(topicSuggestRepository.findAll()).thenReturn(suggests);

        // when
        List<AdminTopicSuggestResponse> result = adminTopicSuggestService.getAllTopicSuggests();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("title");
        assertThat(result.get(0).getDescription()).isEqualTo("desc");
        assertThat(result.get(0).getThumbnailUrl()).isEqualTo("url");
        assertThat(result.get(0).getCreatedAt()).isEqualTo(timeProvider.getCurrentTime());
        assertThat(result.get(0).getStatus()).isEqualTo(TopicSuggestStatus.PENDING.name());
    }

    @Test
    void getSuggestById() {
        // given
        Long id = 1L;
        SiteUser user = new SiteUser("nickname", "foo");

        TopicSuggest topicSuggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .thumbnailUrl("url")
            .status(TopicSuggestStatus.PENDING)
            .build();


        when(topicSuggestRepository.findById(id)).thenReturn(Optional.of(topicSuggest));

        // when
        AdminTopicSuggestResponse result = adminTopicSuggestService.getSuggestById(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getDescription()).isEqualTo("desc");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getThumbnailUrl()).isEqualTo("url");
        assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 1, 1));
    }

    @DisplayName("APPROVE 상태인 문의 수정 시, 토픽 활성화 및 업데이트 성공 응답 리턴")
    @Test
    void patchTopicSuggestStatusWhenApproveStatus() {

        // given
        Long suggestId = 1L;
        TopicSuggestStatus approve = TopicSuggestStatus.APPROVED;
        String rejectReason = "";

        SiteUser user = new SiteUser("nickname", "foo");

        TopicSuggest suggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .thumbnailUrl("url")
            .status(approve)
            .build();

        when(topicSuggestRepository.findById(suggestId)).thenReturn(Optional.of(suggest));

        Topic topic = new Topic(
            suggestId,
            "topic title",
            "topic slug",
            TopicStatus.INACTIVE,
            "url",
            timeProvider.getCurrentTime()
        );

        when(topicRepository.findBySuggestedTopicId(suggestId)).thenReturn(Optional.of(topic));

        // when
        AdminTopicSuggestUpdateResponse result = adminTopicSuggestService.patchTopicSuggestStatus(
            suggestId, approve, rejectReason);

        assertThat(result.getStatus()).isEqualTo(TopicSuggestStatus.APPROVED);
        assertThat(result.getMessage()).isEqualTo("topic is approved");
        assertThat(result.getReason()).isNull();

        assertThat(topic.getStatus()).isEqualTo(TopicStatus.ACTIVE);
    }

    @DisplayName("REJECTED 상태인 문의 수정 시, 토픽 비활성화 및 업데이트 성공 응답 리턴 ")
    @Test
    void patchTopicSuggestStatusWhenRejectStatus() {

        // given
        Long suggestId = 1L;
        TopicSuggestStatus rejected = TopicSuggestStatus.REJECTED;
        String rejectReason = "wrong title";

        SiteUser user = new SiteUser("nickname", "foo");

        TopicSuggest suggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .thumbnailUrl("url")
            .status(rejected)
            .build();

        when(topicSuggestRepository.findById(suggestId)).thenReturn(Optional.of(suggest));

        Topic topic = new Topic(
            suggestId,
            "topic title",
            "topic slug",
            TopicStatus.INACTIVE,
            "url",
            timeProvider.getCurrentTime()
        );

        when(topicRepository.findBySuggestedTopicId(suggestId)).thenReturn(Optional.of(topic));

        // when
        AdminTopicSuggestUpdateResponse result = adminTopicSuggestService.patchTopicSuggestStatus(
            suggestId, rejected, rejectReason);

        assertThat(result.getStatus()).isEqualTo(TopicSuggestStatus.REJECTED);
        assertThat(result.getMessage()).isEqualTo("topic is rejected");
        assertThat(result.getReason()).isEqualTo("wrong title");

        assertThat(topic.getStatus()).isEqualTo(TopicStatus.INACTIVE);
    }
}