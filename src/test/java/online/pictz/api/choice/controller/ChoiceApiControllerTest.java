package online.pictz.api.choice.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import online.pictz.api.choice.dto.ChoiceCountResponse;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.service.ChoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class ChoiceApiControllerTest {

    @Mock
    private ChoiceService choiceService;

    @InjectMocks
    private ChoiceApiController choiceApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChoiceCounts() {

        // given
        List<Long> choiceIds = List.of(1L);

        List<ChoiceCountResponse> serviceMockResponse = List.of(
            new ChoiceCountResponse(1L, 10),
            new ChoiceCountResponse(2L, 20)
        );

        when(choiceService.getChoiceCounts(choiceIds)).thenReturn(serviceMockResponse);

        // when
        ResponseEntity<List<ChoiceCountResponse>> result = choiceApiController.getChoiceCounts(
            choiceIds);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(result.getBody()).size()).isEqualTo(2);
        assertThat(result.getBody().get(0).getChoiceId()).isEqualTo(1L);
        assertThat(result.getBody().get(0).getVoteCount()).isEqualTo(10);
        assertThat(result.getBody().get(1).getChoiceId()).isEqualTo(2L);
        assertThat(result.getBody().get(1).getVoteCount()).isEqualTo(20);
    }

    @Test
    void getChoicesForTopics() {

        // given
        List<Long> topicIds = List.of(1L);
        Long topicId = 1L;
        Long choiceImgId = 1L;
        String name = "choice1";
        String url = "url1";

        List<ChoiceResponse> serviceMockResponse = List.of(new ChoiceResponse(
            new Choice(topicId, name, url, choiceImgId)
        ));

        when(choiceService.getChoiceListByTopicIds(topicIds)).thenReturn(serviceMockResponse);

        // when
        ResponseEntity<List<ChoiceResponse>> result = choiceApiController.getChoicesForTopics(
            topicIds);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().get(0)).isNotNull();
        assertThat(result.getBody()).isEqualTo(serviceMockResponse);
        assertThat(result.getBody().get(0).getTopicId()).isEqualTo(topicId);
        assertThat(result.getBody().get(0).getImageUrl()).isEqualTo(url);
        assertThat(result.getBody().get(0).getName()).isEqualTo(name);

    }
}