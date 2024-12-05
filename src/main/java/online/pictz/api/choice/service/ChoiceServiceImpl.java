package online.pictz.api.choice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceCountResponse;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.exception.TopicNotFound;
import online.pictz.api.topic.repository.TopicRepository;
import online.pictz.api.vote.service.memory.InMemoryChoiceStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChoiceServiceImpl implements ChoiceService {

    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;
    private final InMemoryChoiceStorage choiceStorage;

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
     * 선택지 투표 총 합
     */
    @Transactional(readOnly = true)
    @Override
    public List<ChoiceCountResponse> getChoiceCounts(List<Long> choiceIds) {
        Map<Long, Integer> memoryCountsMap = choiceStorage.getCurrentCounts(choiceIds);
        List<ChoiceCountResponse> dbTotalCounts = choiceRepository.getChoiceTotalCounts(
            choiceIds);

        Map<Long, Integer> dbCountsMap = dbTotalCounts.stream()
            .collect(Collectors.toMap(
                ChoiceCountResponse::getChoiceId,
                ChoiceCountResponse::getVoteCount
            ));

        List<ChoiceCountResponse> result = new ArrayList<>();

        // db count + memory count
        for (Long choiceId : choiceIds) {
            int dbCount = dbCountsMap.getOrDefault(choiceId, 0);
            int memoryCount = memoryCountsMap.getOrDefault(choiceId, 0);
            int totalCount = dbCount + memoryCount;
            result.add(new ChoiceCountResponse(choiceId, totalCount));
        }

        return result;
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
