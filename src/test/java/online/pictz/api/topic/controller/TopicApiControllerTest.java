package online.pictz.api.topic.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.service.ChoiceService;
import online.pictz.api.common.dto.PagedResponse;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.dto.TopicCountResponse;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.service.TopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class TopicApiControllerTest {

    @Mock
    private TopicService topicService;

    @Mock
    private ChoiceService choiceService;

    @InjectMocks
    private TopicApiController topicApiController;

    private TopicResponse topicResponse1;
    private TopicResponse topicResponse2;
    private TimeProvider timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 12, 0));
    private PagedResponse<TopicResponse> pagedResponse;

    int currentPage = 0;
    int pageSize = 3; // page 당 최대 아이템 개수
    int totalElements = 2 ; // 현재 존재하는 아이템 개수
    int totalPages = 1;
    boolean isLast = true;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        topicResponse1 = TopicResponse.builder()
            .id(1L)
            .suggestedTopicId(1L)
            .title("토픽1")
            .slug("slug1")
            .status(TopicStatus.ACTIVE)
            .totalCount(10)
            .thumbnailImageUrl("url1")
            .createdAt(timeProvider.getCurrentTime())
            .build();

        topicResponse2 = TopicResponse.builder()
            .id(2L)
            .suggestedTopicId(2L)
            .title("토픽2")
            .slug("slug2")
            .status(TopicStatus.ACTIVE)
            .totalCount(20)
            .thumbnailImageUrl("url2")
            .createdAt(timeProvider.getCurrentTime())
            .build();


        List<TopicResponse> queryResponse = List.of(topicResponse1, topicResponse2);

        pagedResponse = new PagedResponse<>(queryResponse, currentPage, pageSize, totalElements, totalPages, isLast);
    }


    @DisplayName("인기순 정렬로 활성 토픽 목록을 조회할 수 있다")
    @Test
    void getActiveTopicsWithPopular() {
        //given
        TopicSort popular = TopicSort.POPULAR;

        when(topicService.getActiveTopics(popular, currentPage)).thenReturn(pagedResponse);

        // when
        ResponseEntity<PagedResponse<TopicResponse>> response = topicApiController.getActiveTopics(
            popular, currentPage);

        // then
        assertThat(response.getBody()).isEqualTo(pagedResponse);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @DisplayName("최신순 정렬로 활성 토픽 목록을 조회할 수 있다")
    @Test
    void getActiveTopicsWithLatest() {
        //given
        TopicSort popular = TopicSort.LATEST;

        when(topicService.getActiveTopics(popular, currentPage)).thenReturn(pagedResponse);

        // when
        ResponseEntity<PagedResponse<TopicResponse>> response = topicApiController.getActiveTopics(
            popular, currentPage);

        // then
        assertThat(response.getBody()).isEqualTo(pagedResponse);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @DisplayName("토픽 슬러그로 토픽 선택지 목록을 조회할 수 있다")
    @Test
    void getChoicesForTopicBySlug() {

        // given
        String slug = "slug1";
        String choiceName = "name";
        String imgUrl = "url";
        Long choiceImgId = 1L;

        List<ChoiceResponse> mockChoiceResponse = List.of(
            new ChoiceResponse(new Choice(topicResponse1.getId(), choiceName, imgUrl, choiceImgId))
        );

        when(choiceService.getChoiceListByTopicSlug(slug)).thenReturn(mockChoiceResponse);

        // when
        ResponseEntity<List<ChoiceResponse>> response = topicApiController.getChoicesForTopicBySlug(
            slug);

        // then
        assertThat(response.getBody()).isEqualTo(mockChoiceResponse);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("name");
    }

    @DisplayName("슬러그에 해당하는 토픽이 존재하지 않을 경우 예외 발생")
    @Test
    void getChoicesForTopicBySlug_FailWhenTopicNotExistsWithSlug() {

        // given
        String slug = "slug1";
        String choiceName = "name";
        String imgUrl = "url";
        Long choiceImgId = 1L;

        List<ChoiceResponse> serviceMockResponse = List.of(
            new ChoiceResponse(new Choice(topicResponse1.getId(), choiceName, imgUrl, choiceImgId))
        );

        when(choiceService.getChoiceListByTopicSlug(slug)).thenThrow(TopicNotFound.bySlug(slug));

        // when & then
        assertThatThrownBy(() -> topicApiController.getChoicesForTopicBySlug(slug))
            .isInstanceOf(TopicNotFound.class)
            .hasMessage("Topic with slug not found : " + slug);
    }

    @DisplayName("페이지에 해당하는 토픽 총 투표수 목록을 조회 할 수 있다")
    @Test
    void getTopicCounts() {

        // given
        int currentPage = 0;
        List<TopicCountResponse> serviceMockResponse = List.of(new TopicCountResponse(1L, 10));
        when(topicService.getAllTopicCounts(currentPage)).thenReturn(serviceMockResponse);

        // when
        ResponseEntity<List<TopicCountResponse>> result = topicApiController.getTopicCounts(
            currentPage);

        // then
        assertThat(result.getBody()).isEqualTo(serviceMockResponse);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().get(0).getTotalCount()).isEqualTo(10);
        assertThat(result.getBody().get(0).getId()).isEqualTo(1L);
    }

    @DisplayName("검색어와 정렬 조건으로 토픽을 검색할 수 있다")
    @Test
    void search() {

        // given
        String query = "토픽";
        TopicSort sort = TopicSort.POPULAR;
        int currentPage = 0;

        when(topicService.searchTopics(query, sort, currentPage)).thenReturn(pagedResponse);

        // when
        ResponseEntity<PagedResponse<TopicResponse>> result = topicApiController.search(query, sort,
            currentPage);

        // then
        assertThat(result.getBody()).isEqualTo(pagedResponse);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result)
            .satisfies(responses -> {
                assertThat(responses.getBody().getContent().get(0).getTitle()).isEqualTo("토픽1");
                assertThat(responses.getBody().getContent().get(1).getTitle()).isEqualTo("토픽2");
            });

    }



}