package online.pictz.api.vote.service.optimistic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.entity.Vote;
import org.springframework.stereotype.Component;

@Component
public class VoteConverter {

    /**
     * 투표 요청한 선택지 ID 값 목록 추출
     */
    public List<Long> convertToChoiceIds(List<VoteRequest> voteRequests) {
        return voteRequests.stream()
            .map(VoteRequest::getChoiceId)
            .collect(Collectors.toList());
    }

    /**
     * 투표 정보 목록 entity 로 변환
     */
    public List<Vote> convertToVoteEntities(List<VoteRequest> voteRequests, LocalDateTime voteAt) {
        List<Vote> votes = new ArrayList<>();
        for (VoteRequest vote : voteRequests) {
            Vote voteEntity = Vote.builder()
                .choiceId(vote.getChoiceId())
                .count(vote.getCount())
                .votedAt(voteAt)
                .build();
            votes.add(voteEntity);
        }
        return votes;
    }

    /**
     * choiceId 별 투표 회수 Map 생성
     * choiceId(1) : count(100)
     */
    public Map<Long, Integer> convertToVoteCountMap(List<VoteRequest> voteRequests) {
        Map<Long, Integer> voteCountMap = new ConcurrentHashMap<>();
        for (VoteRequest vote : voteRequests) {
            voteCountMap.put(vote.getChoiceId(), vote.getCount());
        }
        return voteCountMap;
    }


}
