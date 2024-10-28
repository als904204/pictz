package online.pictz.api.vote.service;


import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.network.IpExtractor;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.entity.Vote;
import online.pictz.api.vote.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VoteServiceImpl implements VoteService{

    private final VoteRepository voteRepository;
    private final ChoiceRepository choiceRepository;
    private final IpExtractor ipExtractor;
    private final TimeProvider timeProvider;
    private final VoteConverter voteConverter;
    private final VoteValidator voteValidator;

    @Transactional
    @Override
    public synchronized void voteBulk(List<VoteRequest> voteRequests) {

        // 매크로 검증
        voteValidator.validateVoteMacro(voteRequests);

        // 요청한 선택지 ID 값 목록 추출
        List<Long> choiceIds = voteConverter.convertToChoiceIds(voteRequests);
        List<Choice> choices = choiceRepository.findAllById(choiceIds);

        // 투표 수 choice ID에 맞게 업데이트
        Map<Long, Integer> voteCountMap = voteConverter.convertToVoteCountMap(voteRequests);

        choices.forEach(choice -> {
            int countToAdd = voteCountMap.getOrDefault(choice.getId(),0);
            choice.updateVoteCount(countToAdd);
        });

        choiceRepository.saveAll(choices);

        // 투표 정보 저장
        String ip = ipExtractor.extractIp();
        List<Vote> votesToSave = voteConverter.convertToVoteEntities(voteRequests, ip, timeProvider.getCurrentTime());

        voteRepository.saveAll(votesToSave);

    }

}
