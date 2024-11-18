package online.pictz.api.topic.controller;

import static online.pictz.api.util.TestUtils.createImageFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import online.pictz.api.topic.dto.ChoiceImageRequest;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.dto.TopicSuggestUpdate;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.topic.service.TopicSuggestService;
import online.pictz.api.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

class TopicSuggestApiControllerTest {

    private TopicSuggestApiController topicSuggestApiController;

    @Mock
    private TopicSuggestService topicSuggestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        topicSuggestApiController = new TopicSuggestApiController(topicSuggestService);
    }
    @Test
    void create() {
        // given
        Long userId = 1L;
        String nickname = " foo";
        UserDto userDto = new UserDto(userId, nickname);

        MultipartFile thumbnail = createImageFile("thumbnail1", "thumbnail1.jpg", "image/jpeg",
            "haha1");
        List<MultipartFile> choiceImageFiles = List.of(
            createImageFile("choiceImg2", "choiceImg2.jpg", "image/jpeg",
                "choice2"));

        String title = "title";
        String desc = "desc";

        TopicSuggestCreate suggestCreate = new TopicSuggestCreate(
            title,
            desc,
            thumbnail,
            choiceImageFiles
        );

        TopicSuggestResponse mockResponse = new TopicSuggestResponse(
            1L,
            title,
            desc,
            "보류중",
            LocalDateTime.of(2024, 1, 1, 1, 1),
            null,
            nickname,
            "url",
            null,
            List.of()
        );

        when(topicSuggestService.createSuggest(userId, suggestCreate)).thenReturn(mockResponse);

        // when
        ResponseEntity<TopicSuggestResponse> response = topicSuggestApiController.create(
            userDto, suggestCreate);

        // then
        TopicSuggestResponse body = response.getBody();
        assertThat(body.getTitle()).isEqualTo(title);
        assertThat(body.getContent()).isEqualTo(desc);
        assertThat(body.getThumbnailUrl()).isEqualTo("url");
        assertThat(body.getRejectReason()).isNull();
        assertThat(body.getNickname()).isEqualTo(nickname);
        assertThat(body.getStatus()).isEqualTo("보류중");


    }

    @Test
    void getUserTopicSuggestList() {
        // given
        Long userId = 1L;
        String nickname = "foo";
        UserDto userDto = new UserDto(userId, nickname);

        String title = "title";
        String desc = "desc";

        List<TopicSuggestResponse> mockResponses = List.of(
            new TopicSuggestResponse(
                1L,
                title,
                desc,
                "보류중",
                LocalDateTime.of(2024, 1, 1, 1, 1),
                null,
                nickname,
                "url",
                null,
                List.of()
            )
        );

        when(topicSuggestService.getUserTopicSuggestList(userId)).thenReturn(mockResponses);

        // when
        ResponseEntity<List<TopicSuggestResponse>> response = topicSuggestApiController.getUserTopicSuggestList(
            userDto);

        // then
        List<TopicSuggestResponse> body = response.getBody();

        assertThat(body.get(0).getNickname()).isEqualTo(nickname);
        assertThat(body.get(0).getTitle()).isEqualTo(title);
        assertThat(body.get(0).getContent()).isEqualTo(desc);
        assertThat(body.get(0).getStatus()).isEqualTo("보류중");
        assertThat(body.get(0).getThumbnailUrl()).isEqualTo("url");
    }

    @Test
    void getUserTopicSuggestDetail() {
        // given
        Long userId = 1L;
        Long suggestId = 100L;
        UserDto userDto = new UserDto(userId, "foo");

        TopicSuggestResponse mockResponse = new TopicSuggestResponse(
            suggestId,
            "title",
            "desc",
            "PENDING",
            LocalDateTime.now(),
            LocalDateTime.now(),
            "foo",
            "thumbnailUrl",
            null,
            List.of()
        );

        when(topicSuggestService.getUserTopicSuggestDetail(suggestId, userId)).thenReturn(mockResponse);

        // when
        ResponseEntity<TopicSuggestResponse> result = topicSuggestApiController.getUserTopicSuggestDetail(suggestId, userDto);

        // then
        TopicSuggestResponse body = result.getBody();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(suggestId);
        assertThat(body.getTitle()).isEqualTo("title");
        assertThat(body.getContent()).isEqualTo("desc");
        assertThat(body.getStatus()).isEqualTo(TopicSuggestStatus.PENDING.name());
        assertThat(body.getNickname()).isEqualTo("foo");
        assertThat(body.getThumbnailUrl()).isEqualTo("thumbnailUrl");
        assertThat(body.getChoiceImages()).isEmpty();
    }

    @Test
    void updateUserTopicSuggest() {
        // given
        Long userId = 1L;
        Long suggestId = 100L;
        UserDto userDto = new UserDto(userId, "foo");

        // MockMultipartFile 생성
        MultipartFile newThumbnail = createImageFile("thumbnail", "newThumbnail.jpg", "image/jpeg", "newThumbnailContent");
        MultipartFile choiceImage1 = createImageFile("choiceImg1", "choiceImg1.jpg", "image/jpeg", "choiceImage1Content");

        ChoiceImageRequest choiceImageRequest = new ChoiceImageRequest(200L, choiceImage1);
        List<ChoiceImageRequest> choiceImages = List.of(choiceImageRequest);

        TopicSuggestUpdate updateDto = new TopicSuggestUpdate(
            "newTitle",
            "newDescription",
            newThumbnail,
            choiceImages
        );

        TopicSuggestResponse mockResponse = new TopicSuggestResponse(
            suggestId,
            "newTitle",
            "newDescription",
            TopicSuggestStatus.REJECTED.name(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "foo",
            "newThumbnailUrl",
            null,
            List.of()
        );

        when(topicSuggestService.updateTopicSuggest(suggestId, userId, updateDto)).thenReturn(mockResponse);

        // when
        ResponseEntity<TopicSuggestResponse> result = topicSuggestApiController.updateUserTopicSuggest(suggestId, userDto, updateDto);

        // then
        TopicSuggestResponse body = result.getBody();

        // 응답 검증
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(suggestId);
        assertThat(body.getTitle()).isEqualTo("newTitle");
        assertThat(body.getContent()).isEqualTo("newDescription");
        assertThat(body.getStatus()).isEqualTo(TopicSuggestStatus.REJECTED.name());
        assertThat(body.getNickname()).isEqualTo("foo");
        assertThat(body.getThumbnailUrl()).isEqualTo("newThumbnailUrl");
        assertThat(body.getChoiceImages()).isEmpty();

    }
}