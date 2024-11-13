package online.pictz.api.vote.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.network.IpExtractor;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.topic.repository.TopicRepository;
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
    private final IpExtractor ipExtractor;
    private final TimeProvider timeProvider;
    private final VoteConverter voteConverter;
    private final VoteValidator voteValidator;
    private final TopicRepository topicRepository;
    private static final int MAX_RETRY = 30;

    @Transactional
    @Override
    public void voteBulk(List<VoteRequest> voteRequests) {
        int retryCount = 0;
        while (true) {
            try {
                // 매크로 검증
                voteValidator.validateVoteMacro(voteRequests);

                // 요청한 선택지 ID 값 목록 추출
                List<Long> choiceIds = voteConverter.convertToChoiceIds(voteRequests);
                List<Choice> choices = choiceRepository.findAllById(choiceIds);

                // 투표 수 choice ID에 맞게 업데이트
                Map<Long, Integer> voteCountMap = voteConverter.convertToVoteCountMap(voteRequests);

                // 토픽별 투표 수를 집계하기 위한 맵
                Map<Long, Integer> topicVoteIncrementMap = new HashMap<>();

                choices.forEach(choice -> {
                    int countToAdd = voteCountMap.getOrDefault(choice.getId(),0);
                    choice.updateVoteCount(countToAdd);

                    Long topicId = choice.getTopicId();
                    topicVoteIncrementMap.merge(topicId, countToAdd, Integer::sum);
                });

                choiceRepository.saveAll(choices);

                // 토픽 total count 업데이트 (TODO : 배치나 레디스로 일정시간마다 업데이트 하는 방식으로 변경하는게 좋을 듯)
                for (Map.Entry<Long, Integer> entry : topicVoteIncrementMap.entrySet()) {
                    Long topicId = entry.getKey();
                    Integer increment = entry.getValue();
                    int updatedRows = topicRepository.incrementTotalCount(topicId, increment);
                    if (updatedRows == 0) {
                        throw new OptimisticLockingFailureException("Failed to update totalCount for Topic ID: " + topicId);
                    }
                }

                // 투표 정보 저장
                String ip = ipExtractor.extractIp();
                List<Vote> votesToSave = voteConverter.convertToVoteEntities(voteRequests, ip, timeProvider.getCurrentTime());

                voteRepository.saveAll(votesToSave);
                break;
            } catch (OptimisticLockingFailureException e) {
                ++retryCount;
                if (retryCount > MAX_RETRY) {
                    throw VoteTooManyRequests.of(e.getMessage());
                }
            }
        }
    }
}
