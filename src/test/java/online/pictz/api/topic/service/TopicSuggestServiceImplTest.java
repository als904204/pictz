package online.pictz.api.topic.service;

import static online.pictz.api.util.TestUtils.createImageFile;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.image.service.ImageStorageService;
import online.pictz.api.image.service.LocalImageStorageService;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.dto.ChoiceImageRequest;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.dto.TopicSuggestUpdate;
import online.pictz.api.topic.entity.TopicSuggest;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import online.pictz.api.topic.exception.TopicSuggestBadRequest;
import online.pictz.api.topic.repository.TopicSuggestRepository;
import online.pictz.api.user.entity.SiteUser;
import online.pictz.api.user.repository.SiteUserRepository;
import online.pictz.api.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class TopicSuggestServiceImplTest {

    private TopicSuggestService topicSuggestService;

    @Mock
    private TopicSuggestRepository topicSuggestRepository;

    @Mock
    private SiteUserRepository siteUserRepository;

    @Mock
    private ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        TimeProvider timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 1, 1));
        TopicSuggestResponseConverter converter = new TopicSuggestResponseConverter();

        topicSuggestService = new TopicSuggestServiceImpl(
            topicSuggestRepository,
            siteUserRepository,
            timeProvider,
            imageStorageService,
            converter
        );
    }


    @DisplayName("문의 생성에 성공한다")
    @Test
    void createSuggest() {
        // given
        Long userId = 1L;
        SiteUser user = new SiteUser("foo", "123");

        MultipartFile thumbnail = createImageFile("thumbnail1", "thumbnail1.jpg", "image/jpeg",
            "haha1");

        List<MultipartFile> choiceImages = List.of(
            createImageFile("choiceImg1", "choiceImg1.jpg", "image/jpeg", "choice1"),
            createImageFile("choiceImg2", "choiceImg2.jpg", "image/jpeg", "choice2")
        );

        TopicSuggestCreate create = new TopicSuggestCreate("title", "desc", thumbnail,
            choiceImages);

        when(siteUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        TopicSuggestResponse response = topicSuggestService.createSuggest(userId, create);

        // then
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getContent()).isEqualTo("desc");
        assertThat(response.getStatus()).isEqualTo(TopicSuggestStatus.PENDING.getKorean());
        assertThat(response.getNickname()).isEqualTo("foo");

    }

    @DisplayName("회원ID로 토픽 문의 목록을 조회한다")
    @Test
    void getUserTopicSuggestList() {
        // given
        Long userId = 1L;
        SiteUser user = new SiteUser("nickname", "foo");
        TestUtils.setId(user, userId);

        TopicSuggest suggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .thumbnailUrl("url")
            .status(TopicSuggestStatus.PENDING)
            .build();
        TestUtils.setId(suggest, 1L);

        when(siteUserRepository.findById(userId)).thenReturn(Optional.of(user));
        List<TopicSuggest> suggests = List.of(suggest);

        when(topicSuggestRepository.findByUserId(userId)).thenReturn(suggests);

        // when
        List<TopicSuggestResponse> result = topicSuggestService.getUserTopicSuggestList(userId);

        // then
        assertThat(result.get(0).getTitle()).isEqualTo("title");
        assertThat(result.get(0).getContent()).isEqualTo("desc");
        assertThat(result.get(0).getNickname()).isEqualTo("nickname");
        assertThat(result.get(0).getThumbnailUrl()).isEqualTo("url");
    }

    @Test
    void getUserTopicSuggestDetail() {
        // given
        Long userId = 1L;
        SiteUser user = new SiteUser("nickname", "foo");
        TestUtils.setId(user, userId);

        TopicSuggest suggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .thumbnailUrl("url")
            .status(TopicSuggestStatus.PENDING)
            .build();

        Long suggestId = 1L;
        TestUtils.setId(suggest, suggestId);

        when(siteUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicSuggestRepository.findById(suggestId)).thenReturn(Optional.ofNullable(suggest));

        // when
        TopicSuggestResponse result = topicSuggestService.getUserTopicSuggestDetail(
            suggestId, userId);

        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getContent()).isEqualTo("desc");
        assertThat(result.getNickname()).isEqualTo("nickname");
        assertThat(result.getThumbnailUrl()).isEqualTo("url");
        assertThat(result.getStatus()).isEqualTo(TopicSuggestStatus.PENDING.getKorean());
    }

    @DisplayName("토픽 문의를 성공적으로 수정한다")
    @Test
    void updateTopicSuggest() {
        // given
        Long suggestId = 1L;
        Long userId = 1L;
        Long choiceI1 = 1L;

        SiteUser user = new SiteUser("nickname", "foo");
        TestUtils.setId(user, userId);

        String updateTitle = "updateTitle";
        String updateDescription = "updateDescription";

        MultipartFile thumbnail = createImageFile("thumbnail1", "thumbnail1.jpg", "image/jpeg",
            "haha1");
        MultipartFile choiceImage = createImageFile("choiceImg2", "choiceImg2.jpg", "image/jpeg",
            "choice2");

        List<ChoiceImageRequest> choiceImageRequests = List.of(
            new ChoiceImageRequest(choiceI1, choiceImage)
        );

        TopicSuggestUpdate updateDto = new TopicSuggestUpdate(
            updateTitle,
            updateDescription,
            thumbnail,
            choiceImageRequests
        );

        TopicSuggest suggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .thumbnailUrl("url")
            .status(TopicSuggestStatus.REJECTED)
            .build();

        when(siteUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicSuggestRepository.findById(suggestId)).thenReturn(Optional.ofNullable(suggest));


        // when
        TopicSuggestResponse result = topicSuggestService.updateTopicSuggest(
            suggestId, userId, updateDto);

        // then
        assertThat(result.getTitle()).isEqualTo(updateTitle);
        assertThat(result.getContent()).isEqualTo(updateDescription);
        assertThat(result.getStatus()).isEqualTo(TopicSuggestStatus.PENDING.getKorean());
    }

    @DisplayName("REJECTED 가 아닌 문의는 예외가 발생한다")
    @Test
    void updateTopicSuggestFailWhenStatusIsNotRejected() {
        // given
        Long suggestId = 1L;
        Long userId = 1L;
        Long choiceI1 = 1L;

        SiteUser user = new SiteUser("nickname", "foo");
        TestUtils.setId(user, userId);

        String updateTitle = "updateTitle";
        String updateDescription = "updateDescription";

        MultipartFile thumbnail = createImageFile("thumbnail1", "thumbnail1.jpg", "image/jpeg",
            "haha1");
        MultipartFile choiceImage = createImageFile("choiceImg2", "choiceImg2.jpg", "image/jpeg",
            "choice2");

        List<ChoiceImageRequest> choiceImageRequests = List.of(
            new ChoiceImageRequest(choiceI1, choiceImage)
        );

        TopicSuggestUpdate updateDto = new TopicSuggestUpdate(
            updateTitle,
            updateDescription,
            thumbnail,
            choiceImageRequests
        );

        TopicSuggest suggest = TopicSuggest.builder()
            .title("title")
            .description("desc")
            .user(user)
            .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .thumbnailUrl("url")
            .status(TopicSuggestStatus.APPROVED)
            .build();

        when(siteUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(topicSuggestRepository.findById(suggestId)).thenReturn(Optional.ofNullable(suggest));

        // when
        TopicSuggestBadRequest exception = assertThrows(TopicSuggestBadRequest.class,
            () -> topicSuggestService.updateTopicSuggest(suggestId, userId, updateDto));

        // then
        assertThat(exception.getErrorCode()).isEqualTo("E_403");
        assertThat(exception.getMessage()).isEqualTo("The suggest is not REJECTED status : 승인됨");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }



}