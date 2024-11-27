package online.pictz.api.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import online.pictz.api.admin.dto.AdminTopicSuggestResponse;
import online.pictz.api.admin.dto.AdminTopicSuggestUpdateRequest;
import online.pictz.api.admin.dto.AdminTopicSuggestUpdateResponse;
import online.pictz.api.admin.service.AdminTopicSuggestService;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.dto.TopicSuggestChoiceImageResponse;
import online.pictz.api.topic.entity.TopicSuggestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AdminTopicSuggestControllerTest {

    private AdminTopicSuggestController adminTopicSuggestController;

    private final TimeProvider timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 1, 1));

    @Mock
    private AdminTopicSuggestService adminTopicSuggestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminTopicSuggestController = new AdminTopicSuggestController(adminTopicSuggestService);
    }

    @Test
    void getAllTopicSuggests() {

        // given
        List<AdminTopicSuggestResponse> mockResponse = List.of(
            AdminTopicSuggestResponse.builder()
                .id(1L)
                .title("title")
                .description("desc")
                .thumbnailUrl("url")
                .nickname("nickname")
                .status(TopicSuggestStatus.APPROVED.name())
                .rejectReason("")
                .createdAt(timeProvider.getCurrentTime())
                .choiceImages(
                    List.of(
                        new TopicSuggestChoiceImageResponse(1L, "imgUrl", "imgName")
                    )
                )
                .build()
        );

        when(adminTopicSuggestService.getAllTopicSuggests()).thenReturn(mockResponse);

        // when
        ResponseEntity<List<AdminTopicSuggestResponse>> result = adminTopicSuggestController.getAllTopicSuggests();

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).getId()).isEqualTo(1L);
        assertThat(result.getBody().get(0).getTitle()).isEqualTo("title");
        assertThat(result.getBody().get(0).getDescription()).isEqualTo("desc");
        assertThat(result.getBody().get(0).getThumbnailUrl()).isEqualTo("url");
        assertThat(result.getBody().get(0).getNickname()).isEqualTo("nickname");
        assertThat(result.getBody().get(0).getStatus()).isEqualTo(TopicSuggestStatus.APPROVED.name());
        assertThat(result.getBody().get(0).getRejectReason()).isEqualTo("");
        assertThat(result.getBody().get(0).getCreatedAt()).isEqualTo(timeProvider.getCurrentTime());
        assertThat(result.getBody().get(0).getChoiceImages()).hasSize(1);
        assertThat(result.getBody().get(0).getChoiceImages().get(0).getId()).isEqualTo(1L);
        assertThat(result.getBody().get(0).getChoiceImages().get(0).getImageUrl()).isEqualTo("imgUrl");
        assertThat(result.getBody().get(0).getChoiceImages().get(0).getFileName()).isEqualTo(
            "imgName");

    }

    @Test
    void getTopicSuggestById() {
        // given
        Long id = 1L;

        AdminTopicSuggestResponse mockResponse = AdminTopicSuggestResponse.builder()
            .id(id)
            .title("title")
            .description("desc")
            .thumbnailUrl("url")
            .nickname("nickname")
            .status(TopicSuggestStatus.APPROVED.name())
            .rejectReason("")
            .createdAt(timeProvider.getCurrentTime())
            .choiceImages(
                List.of(
                    new TopicSuggestChoiceImageResponse(1L, "imgUrl", "imgName")
                )
            )
            .build();

        when(adminTopicSuggestService.getSuggestById(id)).thenReturn(mockResponse);

        // when
        ResponseEntity<AdminTopicSuggestResponse> result = adminTopicSuggestController.getTopicSuggestById(
            id);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getTitle()).isEqualTo("title");
        assertThat(result.getBody().getDescription()).isEqualTo("desc");
        assertThat(result.getBody().getThumbnailUrl()).isEqualTo("url");
        assertThat(result.getBody().getNickname()).isEqualTo("nickname");
        assertThat(result.getBody().getStatus()).isEqualTo(TopicSuggestStatus.APPROVED.name());
        assertThat(result.getBody().getRejectReason()).isEqualTo("");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(timeProvider.getCurrentTime());
        assertThat(result.getBody().getChoiceImages()).hasSize(1);
        assertThat(result.getBody().getChoiceImages().get(0).getId()).isEqualTo(1L);
        assertThat(result.getBody().getChoiceImages().get(0).getImageUrl()).isEqualTo("imgUrl");
        assertThat(result.getBody().getChoiceImages().get(0).getFileName()).isEqualTo("imgName");
    }

    @Test
    @DisplayName("토픽 제안 상태를 승인할 때, 올바른 응답을 반환해야 한다.")
    void patchTopicSuggestStatus_Approved() {
        // given
        Long suggestId = 1L;
        TopicSuggestStatus status = TopicSuggestStatus.APPROVED;
        String rejectReason = null; // 승인 시 거부 사유 없음

        AdminTopicSuggestUpdateRequest request = new AdminTopicSuggestUpdateRequest(status, rejectReason);

        AdminTopicSuggestUpdateResponse mockResponse = new AdminTopicSuggestUpdateResponse(status,
            "topic is approved", rejectReason);


        when(adminTopicSuggestService.patchTopicSuggestStatus(suggestId, status, rejectReason))
            .thenReturn(mockResponse);

        // when
        ResponseEntity<AdminTopicSuggestUpdateResponse> response = adminTopicSuggestController.patchTopicSuggestStatus(suggestId, request);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(status);
        assertThat(response.getBody().getMessage()).isEqualTo("topic is approved");
        assertThat(response.getBody().getReason()).isNull();

    }

    @Test
    @DisplayName("토픽 제안 상태를 거부할 때, 서비스가 호출되고, 올바른 응답을 반환해야 한다.")
    void patchTopicSuggestStatus_Rejected() {
        // given
        Long suggestId = 1L;
        TopicSuggestStatus status = TopicSuggestStatus.REJECTED;
        String rejectReason = "wrong title";

        AdminTopicSuggestUpdateRequest request = new AdminTopicSuggestUpdateRequest(status, rejectReason);

        AdminTopicSuggestUpdateResponse mockResponse = new AdminTopicSuggestUpdateResponse(status,
            "topic is rejected", rejectReason);

        when(adminTopicSuggestService.patchTopicSuggestStatus(suggestId, status, rejectReason))
            .thenReturn(mockResponse);

        // when
        ResponseEntity<AdminTopicSuggestUpdateResponse> response = adminTopicSuggestController.patchTopicSuggestStatus(suggestId, request);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(status);
        assertThat(response.getBody().getMessage()).isEqualTo("topic is rejected");
        assertThat(response.getBody().getReason()).isEqualTo(rejectReason);
    }

}