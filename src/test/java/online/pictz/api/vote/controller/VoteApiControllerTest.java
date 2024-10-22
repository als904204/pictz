package online.pictz.api.vote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import online.pictz.api.vote.dto.VoteCreate;
import online.pictz.api.vote.dto.VoteResponse;
import online.pictz.api.vote.service.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class VoteApiControllerTest {

    @Mock
    private VoteService voteService;

    @InjectMocks
    private VoteApiController voteApiController;

    @DisplayName("투표 생성 성공")
    @Test
    void createVote_SUCCESS() {

        // given
        var choiceId = 1L;
        VoteCreate voteCreateRequest = new VoteCreate(choiceId);
        VoteResponse voteCreateResponse = new VoteResponse(choiceId, 999);

        when(voteService.createVote(voteCreateRequest)).thenReturn(voteCreateResponse);

        // when
        ResponseEntity<VoteResponse> apiResult = voteApiController.createVote(voteCreateRequest);

        // then
        assertThat(apiResult.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(apiResult.getBody().getChoiceId()).isEqualTo(1L);
        assertThat(apiResult.getBody().getVoteCount()).isEqualTo(999);
        assertThat(voteCreateResponse).isEqualTo(apiResult.getBody());
    }

}