package online.pictz.api.vote.service;


import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.topic.service.TopicService;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.entity.Vote;
import online.pictz.api.vote.exception.VoteTooManyRequests;
import online.pictz.api.vote.repository.VoteRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VoteServiceImpl implements VoteService{

    private final VoteRepository voteRepository;
    private final ChoiceRepository choiceRepository;
    private final TimeProvider timeProvider;
    private final VoteConverter voteConverter;
    private final VoteValidator voteValidator;
    private final VoteProcessor voteProcessor;
    private final TopicService topicService;
    private static final int MAX_RETRY = 30;

    /**
     * 투표 저장
     * 멀티 쓰레드 동시성 문제 및 성능, DB 과부하 문제
     * @param voteRequests
     */
    @Deprecated
    @Transactional
    @Override
    public void voteBulk(List<VoteRequest> voteRequests) {
        int retryCount = 0;
        while (true) {
            try {
                // 매크로 검증
                voteValidator.validateVoteMacro(voteRequests);

                // 선택지별(Choice) 투표수 업데이트
                List<Choice> existingChoices = getChoicesFromVoteRequest(voteRequests);
                existingChoices = voteProcessor.updateChoiceCount(existingChoices, voteRequests);
                choiceRepository.saveAll(existingChoices);

                // 토픽(Topic) 총 투표수 업데이트
                Map<Long, Integer> topicVoteIncrements = voteProcessor.calculateTopicVoteTotalCount(existingChoices, voteRequests);
                topicService.updateTopicTotalCounts(topicVoteIncrements);

                // 투표(Vote) 정보 저장
                List<Vote> votes = voteConverter.convertToVoteEntities(voteRequests, timeProvider.getCurrentTime());
                voteRepository.saveAll(votes);

                break;
            } catch (OptimisticLockingFailureException e) {
                ++retryCount;
                if (retryCount > MAX_RETRY) {
                    throw VoteTooManyRequests.of(e.getMessage());
                }
            }
        }
    }

    /**
     * 투표 요청으로부터 선택지 목록 조회
     *
     * @param voteRequests 투표 요청 목록
     * @return 선택지 목록
     */
    private List<Choice> getChoicesFromVoteRequest(List<VoteRequest> voteRequests) {
        List<Long> choiceIds = voteConverter.convertToChoiceIds(voteRequests);
        return choiceRepository.findAllById(choiceIds);
    }
}
