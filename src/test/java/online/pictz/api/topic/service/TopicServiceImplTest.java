package online.pictz.api.topic.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import online.pictz.api.common.dto.PagedResponse;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.dto.TopicCountResponse;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicSort;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class TopicServiceImplTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicServiceImpl topicService;


    private TestTimeProvider fixedProvider;
    private static final int PAGE_ITEM_SIZE = 3; // 한 페이지당 최대 item 개수
    private static final Long TOTAL = 2L;        // 현재 페이지에 해당하는 item 개수
    private TopicResponse topicResponse1;        // 현재 페이지에 존재하는 item1
    private TopicResponse topicResponse2;        // 현재 페이지에 존재하는 item2


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.fixedProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 12, 0));

        this.topicResponse1 = TopicResponse.builder()
            .id(1L)
            .suggestedTopicId(1L)
            .title("토픽1")
            .slug("슬러그1")
            .status(TopicStatus.ACTIVE)
            .totalCount(10)
            .thumbnailImageUrl("썸네일1")
            .createdAt(fixedProvider.getCurrentTime())
            .build();

        this.topicResponse2 = TopicResponse.builder()
            .id(2L)
            .suggestedTopicId(2L)
            .title("토픽2")
            .slug("슬러그2")
            .status(TopicStatus.ACTIVE)
            .totalCount(0)
            .thumbnailImageUrl("썸네일2")
            .createdAt(fixedProvider.getCurrentTime())
            .build();
    }

    @Test
    void getActiveTopics() {

        // given
        TopicSort popular = TopicSort.POPULAR;
        int currentPage = 0;

        List<TopicResponse> queryResult = List.of(
            topicResponse1,
            topicResponse2
        );

        PageImpl<TopicResponse> queryResponse = new PageImpl<>(queryResult,
            PageRequest.of(currentPage, PAGE_ITEM_SIZE), TOTAL);

        when(topicRepository.findActiveTopics(popular, currentPage)).thenReturn(queryResponse);

        // when
        PagedResponse<TopicResponse> result = topicService.getActiveTopics(popular, currentPage);

        // then
        assertThat(result.getContent()).isEqualTo(queryResult);
        assertThat(result.getCurrentPage()).isEqualTo(currentPage);
        assertThat(result.getPageSize()).isEqualTo(PAGE_ITEM_SIZE);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(TOTAL);
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void getAllTopicCounts() {

        // given
        List<TopicCountResponse> queryResponse = List.of(
            new TopicCountResponse(1L, 10),
            new TopicCountResponse(2L, 20)
        );

        int currentPage = 0;
        when(topicRepository.getTopicTotalCounts(currentPage)).thenReturn(queryResponse);

        // when
        List<TopicCountResponse> result = topicService.getAllTopicCounts(currentPage);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(queryResponse);
        assertThat(result)
            .satisfies(responses -> {
                assertThat(responses.get(0).getId()).isEqualTo(1L);
                assertThat(responses.get(0).getTotalCount()).isEqualTo(10);
                assertThat(responses.get(1).getId()).isEqualTo(2L);
                assertThat(responses.get(1).getTotalCount()).isEqualTo(20);
            });
    }

    @Test
    void searchTopics() {

        // given
        String query = "hello";
        TopicSort popular = TopicSort.POPULAR;
        int currentPage = 0;

        List<TopicResponse> queryResult = List.of(
            topicResponse1,
            topicResponse2
        );

        PageImpl<TopicResponse> queryResponse = new PageImpl<>(queryResult,
            PageRequest.of(currentPage, PAGE_ITEM_SIZE), TOTAL);

        when(topicRepository.searchTopics(query, popular, currentPage)).thenReturn(queryResponse);

        // when
        PagedResponse<TopicResponse> result = topicService.searchTopics(query,
            popular, currentPage);

        // then
        assertThat(result.getContent()).isEqualTo(queryResult);
        assertThat(result.getCurrentPage()).isEqualTo(currentPage);
        assertThat(result.getPageSize()).isEqualTo(PAGE_ITEM_SIZE);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(TOTAL);
        assertThat(result.isLast()).isTrue();
    }
}