package online.pictz.api.vote.service.test;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.service.VoteService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@RequiredArgsConstructor
@Service
public class VoteInMemoryService implements VoteService {

    private final InMemoryChoiceStorage choiceStorage;

    @Override
    public void voteBulk(List<VoteRequest> voteRequests) {
        for (VoteRequest request : voteRequests) {
            choiceStorage.store(request.getChoiceId(), request.getCount());
        }
    }

}
