package online.pictz.api.choice.service;

import java.util.List;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.dto.ChoiceCountResponse;

public interface ChoiceService {

    /**
     * 여러 토픽에 관한 선택지 목록 조회
     */
    List<ChoiceResponse> getChoiceListByTopicIds(List<Long> topicIds);

    /**
     * 선택지 투표 수 조회
     */
    List<ChoiceCountResponse> getChoiceCounts(List<Long> id);

    /**
     * 토픽 slug로 선택지 목록 조회
     */
    List<ChoiceResponse> getChoiceListByTopicSlug(String slug);
}
