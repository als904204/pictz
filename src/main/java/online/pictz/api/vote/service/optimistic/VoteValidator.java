package online.pictz.api.vote.service.optimistic;

import java.util.List;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.exception.VoteTooManyRequests;
import org.springframework.stereotype.Component;

@Component
public class VoteValidator {

    private static final int MAX_ALLOWED_VOTE_COUNT = 300;

    /**
     * 비정상적인 투표 클릭 횟수 매크로 감지
     */
    public void validateVoteMacro(List<VoteRequest> voteRequests) {

        int totalVoteCount = 0;

        for(VoteRequest voteRequest : voteRequests) {
            totalVoteCount += voteRequest.getCount();
        }

        if (totalVoteCount > MAX_ALLOWED_VOTE_COUNT) {
            throw VoteTooManyRequests.of(totalVoteCount);
        }
    }

}
