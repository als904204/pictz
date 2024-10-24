package online.pictz.api.choice.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceVoteResult;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.exception.ChoiceNotFound;
import online.pictz.api.choice.repository.ChoiceRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChoiceServiceImpl implements ChoiceService {

    private final ChoiceRepository choiceRepository;

    /**
     * 토픽에 관한 선택지 목록 조회
     */
    @Override
    public List<ChoiceResponse> getChoiceListByTopicId(Long topicId) {
        List<Choice> choiceList = choiceRepository.findByTopicId(topicId);
        if (choiceList.isEmpty()) {
            throw ChoiceNotFound.forTopicId(topicId);
        }
        return choiceList.stream()
            .map(ChoiceResponse::new)
            .toList();
    }

    /**
     * 여러 토픽에 관한 선택지 목록 조회
     */
    @Override
    public List<ChoiceResponse> getChoiceListByTopicIds(List<Long> topicIds) {
        return choiceRepository.findByTopicIdIn(topicIds)
            .stream()
            .map(ChoiceResponse::new)
            .toList();
    }

    /**
     * 선택지 투표 결과 조회
     */
    @Override
    public ChoiceVoteResult getChoiceVoteResultById(Long id) {
        Choice choice = choiceRepository.findById(id)
            .orElseThrow(() -> ChoiceNotFound.forChoiceId(id));
        return new ChoiceVoteResult(choice.getName(), choice.getVoteCount());
    }

}
