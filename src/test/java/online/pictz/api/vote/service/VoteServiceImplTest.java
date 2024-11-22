package online.pictz.api.vote.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.service.TopicService;
import online.pictz.api.util.TestUtils;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.exception.VoteTooManyRequests;
import online.pictz.api.vote.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class VoteServiceImplTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private ChoiceRepository choiceRepository;
    @Mock
    private TopicService topicService;

    private VoteService voteService;

    private VoteValidator voteValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        TimeProvider timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 1, 1));
        VoteConverter voteConverter = new VoteConverter();
        VoteProcessor voteProcessor = new VoteProcessor(voteConverter);

        voteValidator = new VoteValidator();

        voteService = new VoteServiceImpl(
            voteRepository,
            choiceRepository,
            timeProvider,
            voteConverter,
            voteValidator,
            voteProcessor,
            topicService
        );
    }


    @DisplayName("정상적인 투표 요청일 경우 투표 정보가 업데이트 된다")
    @Test
    void voteBulk() {
        // given
        Choice choice;
        Long choiceId = 1L;
        int choiceVoteCount = 100;
        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, choiceVoteCount));

        List<Long> choiceIds = List.of(choiceId);
        Long topicId = 1L;
        Long imgId = 1L;
        List<Choice> choices = List.of(choice = new Choice(topicId, "choice1", "url1", imgId));
        TestUtils.setId(choice, choiceId);

        when(choiceRepository.findAllById(choiceIds)).thenReturn(choices);

        // when
        assertDoesNotThrow(() -> voteService.voteBulk(voteRequests));
        assertThat(choice.getCount()).isEqualTo(100);
    }

    @DisplayName("한번에 많은 투표수를 요청할경우 예외가 발생한다")
    @Test
    void voteBulk_macro_exception() {
        // given
        Long choiceId = 1L;
        int choiceVoteCount = 999_999;
        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, choiceVoteCount));

        VoteTooManyRequests exception = assertThrows(VoteTooManyRequests.class,
            () -> voteValidator.validateVoteMacro(voteRequests));

        assertThat(exception.getMessage()).isEqualTo("Too many vote requests : 999999");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

}