package online.pictz.api.vote.service.optimistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.vote.dto.VoteRequest;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VoteProcessor {

    private final VoteConverter voteConverter;

    /**
     * 선택지의 투표수를 업데이트
     *
     * @param choices      기존 선택지 목록
     * @param voteRequests 투표 요청 목록
     * @return 투표수가 업데이트된 선택지 목록
     */
    public List<Choice> updateChoiceCount(List<Choice> choices, List<VoteRequest> voteRequests) {

        // (key: choiceId, value: Choice)
        Map<Long, Choice> choiceMap = new HashMap<>();
        for (Choice choice : choices) {
            choiceMap.put(choice.getId(), choice);
        }

        // 투표 요청을 기반으로 선택지의 투표수 업데이트
        for (VoteRequest vote : voteRequests) {
            Choice choice = choiceMap.get(vote.getChoiceId());
            if (choice != null) {
                choice.updateCount(vote.getCount());
            }
        }

        return new ArrayList<>(choiceMap.values());
    }


    /**
     * 각 토픽별로 현재 투표에서 증가한 투표수 계산
     *
     * @param choices      기존 선택지 목록
     * @param voteRequests 투표 요청 목록
     * @return 토픽별 투표수 증가량 맵
     */
    public Map<Long, Integer> calculateTopicVoteTotalCount(List<Choice> choices,
        List<VoteRequest> voteRequests) {

        // 선택지 ID와 토픽 ID 매핑 (choiceId -> topicId)
        Map<Long, Long> choiceToTopicMap = choices.stream()
            .collect(Collectors.toMap(
                Choice::getId,
                Choice::getTopicId
            ));

        // 토픽별 투표수 증가량 계산
        Map<Long, Integer> topicVoteTotalMap = new HashMap<>();
        for (VoteRequest voteRequest : voteRequests) {
            Long choiceId = voteRequest.getChoiceId();
            Long topicId = choiceToTopicMap.get(choiceId);
            int count = voteRequest.getCount();

            if (topicId != null) {
                topicVoteTotalMap.merge(topicId, count, Integer::sum);
            }
        }

        return topicVoteTotalMap;
    }


}
