package online.pictz.api.choice.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceVoteResult;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.exception.ChoiceNotFound;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChoiceServiceImpl implements ChoiceService {

    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;

    /**
     * 여러 토픽에 관한 선택지 목록 조회
     */
    @Override
    public List<ChoiceResponse> getChoiceListByTopicIds(List<Long> topicIds) {
        return choiceRepository.findByTopicIdIn(topicIds)
            .stream()
            .map(ChoiceResponse::new)
            .collect(Collectors.toList());
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

    /**
     * 토픽 slug로 선택지 목록 조회
     */
    @Override
    public List<ChoiceResponse> getChoiceListByTopicSlug(String slug) {
        Topic topic = topicRepository.findBySlug(slug).orElseThrow(() -> TopicNotFound.bySlug(slug));
        List<Choice> choices = choiceRepository.findByTopicId(topic.getId());
        return choices.stream()
            .map(ChoiceResponse::new)
            .collect(Collectors.toList());
    }

}
