package online.pictz.api.vote.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.exception.ChoiceNotFound;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.network.IpExtractor;
import online.pictz.api.vote.dto.VoteCreate;
import online.pictz.api.vote.dto.VoteResponse;
import online.pictz.api.mock.FakeIpExtractor;
import online.pictz.api.vote.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ChoiceRepository choiceRepository;

    private IpExtractor ipExtractor;

    private VoteServiceImpl voteService;

    @BeforeEach
    public void setUp() {
        ipExtractor = new FakeIpExtractor("1.1.1.1");
        voteService = new VoteServiceImpl(voteRepository, choiceRepository, ipExtractor);
    }


    @DisplayName("투표 생성 성공 후 choice의 투표 count는 1이다")
    @Test
    void createVote_SUCCESS() {

        // given
        var choiceId = 1L;
        VoteCreate voteCreate = new VoteCreate(choiceId);

        Choice choice = new Choice(1L, "메호대전", "선택지 썸네일 URL");
        when(choiceRepository.findById(anyLong())).thenReturn(Optional.of(choice));

        // when
        VoteResponse voteResponse = voteService.createVote(voteCreate);

        // then
        assertThat(voteResponse.getVoteCount()).isEqualTo(1);

    }

    @DisplayName("투표 하려는 choice가 존재하지 않을 경우 예외 발생")
    @Test
    void createVoid_FAIL_When_choice_is_not_exists() {

        // given
        var choiceId = 9999L;
        VoteCreate voteCreate = new VoteCreate(choiceId);

        when(choiceRepository.findById(choiceId)).thenReturn(Optional.empty());

        // when
        ChoiceNotFound exception = assertThrows(
            ChoiceNotFound.class,
            () -> voteService.createVote(voteCreate)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo("E_404");
        assertThat(exception.getMessage()).isEqualTo("No choice found with ID: 9999");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);

    }
}