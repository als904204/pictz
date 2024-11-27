package online.pictz.api.choice.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import online.pictz.api.choice.dto.ChoiceCountResponse;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.repository.TopicRepository;
import online.pictz.api.util.TestUtils;
import online.pictz.api.vote.service.memory.InMemoryChoiceStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ChoiceServiceImplTest {

    @Mock
    private ChoiceRepository choiceRepository;
    @Mock
    private TopicRepository topicRepository;

    private ChoiceServiceImpl choiceService;

    private TestTimeProvider timeProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 1, 1));
        InMemoryChoiceStorage memoryChoiceStorage = new InMemoryChoiceStorage();

        choiceService = new ChoiceServiceImpl(choiceRepository, topicRepository,
            memoryChoiceStorage);


    }

    @DisplayName("여러 토픽에 관한 선택지 목록 조회")
    @Test
    void getChoiceListByTopicIds() {

        // given
        Long topicId = 1L;

        String name1 = "choice1";
        String imgUrl1 = "url1";
        Long imgId1 = 1L;

        String name2 = "choice2";
        String imgUrl2 = "url2";
        Long imgId2 = 2L;

        List<Choice> choices = List.of(
            new Choice(topicId, name1, imgUrl1, imgId1),
            new Choice(topicId, name2, imgUrl2, imgId2)
        );

        List<Long> topicIds = List.of(topicId);
        when(choiceRepository.findByTopicIdIn(topicIds)).thenReturn(choices);

        // when
        List<ChoiceResponse> result = choiceService.getChoiceListByTopicIds(topicIds);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
            .satisfies(results -> {
                assertThat(result.get(0).getTopicId()).isEqualTo(1L);
                assertThat(result.get(0).getName()).isEqualTo("choice1");
                assertThat(result.get(0).getVoteCount()).isEqualTo(0);
                assertThat(result.get(0).getImageUrl()).isEqualTo("url1");
                assertThat(result.get(1).getTopicId()).isEqualTo(1L);
                assertThat(result.get(1).getName()).isEqualTo("choice2");
                assertThat(result.get(1).getVoteCount()).isEqualTo(0);
                assertThat(result.get(1).getImageUrl()).isEqualTo("url2");
            });
    }

    @DisplayName("선택지 목록에 대한 총 투표수를 조회한다")
    @Test
    void getChoiceCounts() {

        // given
        Long choiceId1 = 1L;
        Long choiceId2 = 2L;
        List<Long> choiceIds = List.of(choiceId1, choiceId2);

        List<ChoiceCountResponse> serviceMockResponse = List.of(
            new ChoiceCountResponse(choiceId1, 10),
            new ChoiceCountResponse(choiceId2, 20)
        );

        when(choiceRepository.getChoiceTotalCounts(choiceIds)).thenReturn(serviceMockResponse);

        choiceService.getChoiceCounts(choiceIds);

    }

    @DisplayName("슬러그로 토픽 선택지 목록을 조회 할 수 있다")
    @Test
    void getChoiceListByTopicSlug() {

        // given
        String slug1 = "slug1";
        Long topicId = 1L;

        Topic topic = Topic.builder()
            .suggestedTopicId(1L)
            .title("title")
            .slug(slug1)
            .status(TopicStatus.ACTIVE)
            .thumbnailImageUrl("url")
            .createdAt(timeProvider.getCurrentTime())
            .build();
        TestUtils.setId(topic, topicId);

        List<Choice> choices = List.of(
            new Choice(topicId, "choice1", "url1", 1L),
            new Choice(topicId, "choice2", "url2", 2L)
        );

        when(topicRepository.findBySlug(slug1)).thenReturn(Optional.ofNullable(topic));
        when(choiceRepository.findByTopicId(topic.getId())).thenReturn(choices);

        // when
        List<ChoiceResponse> result = choiceService.getChoiceListByTopicSlug(slug1);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
            .satisfies(results -> {
                assertThat(results.get(0).getVoteCount()).isZero();
                assertThat(results.get(0).getName()).isEqualTo("choice1");
                assertThat(results.get(0).getImageUrl()).isEqualTo("url1");
                assertThat(results.get(0).getTopicId()).isEqualTo(topicId);
                assertThat(results.get(1).getVoteCount()).isZero();
                assertThat(results.get(1).getName()).isEqualTo("choice2");
                assertThat(results.get(1).getImageUrl()).isEqualTo("url2");
                assertThat(results.get(1).getTopicId()).isEqualTo(topicId);
            });
    }

    @DisplayName("슬러그로 토픽을 찾을 수 없을 때 예외가 발생한다")
    @Test
    void getChoiceListByTopicSlug_TopicNotFound() {
        // given
        String slug = "not exists slug";
        when(topicRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TopicNotFound.class, () -> choiceService.getChoiceListByTopicSlug(slug));
    }
}