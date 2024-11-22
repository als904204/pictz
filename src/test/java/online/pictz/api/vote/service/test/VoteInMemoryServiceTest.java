package online.pictz.api.vote.service.test;

import java.util.List;
import online.pictz.api.vote.dto.VoteRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class VoteInMemoryServiceTest {

    @Autowired
    VoteInMemoryService voteInMemoryService;

    @Autowired
    VoteBatch voteBatch;
    @Test
    void test() {
        Long choiceId1 = 1L;
        Long choiceId2 = 2L;
        Long choiceId3 = 3L;

        int count1 = 10;
        int count2 = 20;
        int count3 = 30;

        List<VoteRequest> voteRequests = List.of(
            new VoteRequest(choiceId1, count1),
            new VoteRequest(choiceId2, count2),
            new VoteRequest(choiceId3, count3)
        );

        voteInMemoryService.voteBulk(
            voteRequests
        );

        voteBatch.processBatchVotes();
    }

}