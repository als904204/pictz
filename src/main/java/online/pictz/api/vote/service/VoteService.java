package online.pictz.api.vote.service;

import online.pictz.api.vote.dto.VoteCreate;
import online.pictz.api.vote.dto.VoteResponse;

public interface VoteService {
    VoteResponse vote(VoteCreate voteCreate);
}
