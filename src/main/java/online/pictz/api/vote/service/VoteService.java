package online.pictz.api.vote.service;

import java.util.List;
import online.pictz.api.vote.dto.VoteRequest;

public interface VoteService {

    void voteBulk(List<VoteRequest> voteRequest);

}
