package online.pictz.api.vote.service;

import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.exception.ChoiceNotFound;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.exception.util.IpExtractor;
import online.pictz.api.vote.dto.VoteCreate;
import online.pictz.api.vote.dto.VoteResponse;
import online.pictz.api.vote.entity.Vote;
import online.pictz.api.vote.repository.VoteRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VoteServiceImpl implements VoteService{

    private final VoteRepository voteRepository;
    private final ChoiceRepository choiceRepository;
    private final IpExtractor ipExtractor;

    @Override
    public VoteResponse vote(VoteCreate voteCreate) {

        Choice choice = choiceRepository.findById(voteCreate.getChoiceId())
            .orElseThrow(() -> ChoiceNotFound.forChoiceId(voteCreate.getChoiceId()));

        choice.incrementVoteCount();
        choiceRepository.save(choice);

        String ipAddress = ipExtractor.extractIp();

        Vote vote = Vote.builder()
            .choiceId(voteCreate.getChoiceId())
            .ip(ipAddress)
            .build();

        voteRepository.save(vote);

        return new VoteResponse(choice.getId(), choice.getVoteCount());
    }
}
