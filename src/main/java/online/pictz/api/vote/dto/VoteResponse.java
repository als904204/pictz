package online.pictz.api.vote.dto;

import lombok.Getter;

@Getter
public class VoteResponse {

    private Long choiceId;
    private int voteCount;

    public VoteResponse(Long choiceId, int voteCount) {
        this.choiceId = choiceId;
        this.voteCount = voteCount;
    }

}
