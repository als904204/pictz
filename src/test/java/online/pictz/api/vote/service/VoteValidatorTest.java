package online.pictz.api.vote.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.exception.VoteTooManyRequests;
import online.pictz.api.vote.service.optimistic.VoteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class VoteValidatorTest {

    private VoteValidator voteValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        voteValidator = new VoteValidator();
    }

    @DisplayName("정상적인 투표 요청일 경우 예외가 발생하지 않는다")
    @Test
    void validateVoteMacro() {
        // given
        Long choiceId = 1L;
        int count = 10;

        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, count));

        // when & then
        assertDoesNotThrow(() -> voteValidator.validateVoteMacro(voteRequests));
    }

    @DisplayName("비정상적인 투표 요청일 경우 예외가 발생한다")
    @Test
    void validateVoteMacroFailWithTooManyRequest() {
        // given
        Long choiceId = 1L;
        int count = 999_999;

        List<VoteRequest> tooManyRequests = List.of(new VoteRequest(choiceId, count));

        // when
        VoteTooManyRequests exception = assertThrows(VoteTooManyRequests.class,
            () -> voteValidator.validateVoteMacro(tooManyRequests));

        // then
        assertThat(exception.getErrorCode()).isEqualTo("E_429");
        assertThat(exception.getMessage()).isEqualTo("Too many vote requests : 999999");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }
}