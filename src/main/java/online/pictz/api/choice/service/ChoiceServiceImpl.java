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
     * Topic에 관한 choice 목록 가져오기
     */
    @Override
    public List<ChoiceResponse> getChoiceListByTopicId(Long topicId) {
        List<Choice> choiceList = choiceRepository.findByTopicId(topicId);
        return choiceList.stream()
            .map(ChoiceResponse::new)
            .toList();
    }

    /**
     * Choice의 투표 수 조회
     *
     * @param id 조회할 Choice의 ID
     * @return Choice의 이름과 투표 수를 포함한 결과
     * @throws ChoiceNotFound 존재하지 않는 Choice ID일 경우
     */
    @Override
    public ChoiceVoteResult getChoiceVoteResultById(Long id) {
        Choice choice = choiceRepository.findById(id)
            .orElseThrow(() -> new ChoiceNotFound(id));
        return new ChoiceVoteResult(choice.getName(), choice.getVoteCount());
    }

}
