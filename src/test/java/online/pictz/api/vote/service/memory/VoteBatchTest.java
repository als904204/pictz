package online.pictz.api.vote.service.memory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class VoteBatchTest {

    private VoteBatch voteBatch;

    private InMemoryChoiceStorage memoryChoiceStorage;

    @Mock
    private ChoiceRepository choiceRepository;

    @Mock
    private VoteBatchProcessor voteBatchProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        TimeProvider timeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 1, 1));
        memoryChoiceStorage = new InMemoryChoiceStorage();

        voteBatch = new VoteBatch(
            voteBatchProcessor,
            choiceRepository,
            timeProvider,
            memoryChoiceStorage
        );
    }

    @Test
    void processBatchVotes() {
        // given
        Choice choice1;
        Choice choice2;

        memoryChoiceStorage.store(1L, 10);
        memoryChoiceStorage.store(2L, 20);

        List<Choice> choices = List.of(
            choice1 = new Choice(1L, "choice1", "url1", 1L),
            choice2 = new Choice(1L, "choice2", "url2", 2L)
        );
        TestUtils.setId(choice1, 1L);
        TestUtils.setId(choice2, 2L);

        when(choiceRepository.findByIdIn(List.of(1L, 2L))).thenReturn(choices);

        // when
        voteBatch.processBatchVotes();

        // then
        assertThat(memoryChoiceStorage.getCount(1L)).isZero();
        assertThat(memoryChoiceStorage.getCount(2L)).isZero();
    }

}