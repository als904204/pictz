package online.pictz.api.vote.service;

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
    public List<Vote> convertToVoteEntities(List<VoteRequest> voteRequests, String ip, LocalDateTime voteAt) {
        List<Vote> votesToSave = new ArrayList<>();
        for (VoteRequest vote : voteRequests) {
            Vote voteEntity = Vote.builder()
                .choiceId(vote.getChoiceId())
                .count(vote.getCount())
                .ip(ip)
                .votedAt(voteAt)
                .build();
            votesToSave.add(voteEntity);
        }
        return votesToSave;
    }

    /**
     * choiceId 별 투표 회수 Map 생성
     */
    public Map<Long, Integer> convertToVoteCountMap(List<VoteRequest> voteRequests) {
        Map<Long, Integer> voteCountMap = new ConcurrentHashMap<>();
        for (VoteRequest vote : voteRequests) {
            voteCountMap.put(vote.getChoiceId(), vote.getCount());
        }
        return voteCountMap;
    }


}
