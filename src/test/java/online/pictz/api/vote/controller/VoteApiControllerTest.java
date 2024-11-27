package online.pictz.api.vote.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class VoteApiControllerTest {

    @Mock
    private VoteService voteService;

    private VoteApiController voteApiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        voteApiController = new VoteApiController(voteService);
    }

    @Test
    @DisplayName("투표 요청은 204 응답을 반환해야 한다.")
    void voteBulk_ShouldCallServiceAndReturnNoContent() {

        // given
        Long choiceId = 1L;
        int count = 100;

        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, count));

        // when
        ResponseEntity<Void> result = voteApiController.voteBulk(voteRequests);

        // then
        assertThat(result.getBody()).isNull();
        assertThat(result.getStatusCodeValue()).isEqualTo(204);

    }

}