package online.pictz.api.choice.service;

import java.util.List;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.dto.ChoiceVoteResult;

public interface ChoiceService {


    /**
     * topic에 관한 choice 목록 가져오기
     */
    List<ChoiceResponse> getChoiceListByTopicId(Long topicId);

    /**
     * Choice 투표 count 결과
     */
    ChoiceVoteResult getChoiceVoteResultById(Long id);
}
