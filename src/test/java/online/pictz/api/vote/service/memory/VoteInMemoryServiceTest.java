package online.pictz.api.vote.service.memory;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import java.util.Map;
import online.pictz.api.vote.dto.VoteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VoteInMemoryServiceTest {

    private VoteInMemoryService voteInMemoryService;
    private InMemoryChoiceStorage memoryChoiceStorage;

    @BeforeEach
    void setUp() {
        memoryChoiceStorage = new InMemoryChoiceStorage();
        voteInMemoryService = new VoteInMemoryService(memoryChoiceStorage);
    }

    @Test
    void voteBulk() {
        // given
        Long choiceId = 1L;
        int count = 10;

        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, count));

        // when
        voteInMemoryService.voteBulk(voteRequests);

        // then
        Map<Long, Integer> result = memoryChoiceStorage.getAndClearStorage();

        assertThat(result.get(choiceId)).isEqualTo(count);

    }

}