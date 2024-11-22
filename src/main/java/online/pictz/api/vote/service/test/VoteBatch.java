package online.pictz.api.vote.service.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.time.TimeProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class VoteBatch {

    private final VoteBatchProcessor voteBatchProcessor;
    private final ChoiceRepository choiceRepository;
    private final TimeProvider timeProvider;
    private final InMemoryChoiceStorage choiceStorage;

    @Scheduled(fixedRate = 15000)
    @Transactional
    public void processBatchVotes() {

        log.info("Start the vote batch...");
        // 인 메모리 투표 결과 가져오기
        Map<Long, Integer> inMemoryChoices = choiceStorage.getAndClearStorage();
        if (inMemoryChoices.isEmpty()) {
            log.info("There is no voting data in memory");
            log.info("Exit the batch");
            return;
        }

        List<Long> requestedChoiceIds = new ArrayList<>(inMemoryChoices.keySet());

        // DB 존재하지 않는 선택지 필터링
        List<Choice> existsChoices = choiceRepository.findByIdIn(requestedChoiceIds);
        Map<Long, Integer> existsChoicesMap = new HashMap<>();

        for (Choice choice : existsChoices) {
            Long choiceId = choice.getId();
            if (inMemoryChoices.containsKey(choiceId)) {
                Integer count = inMemoryChoices.get(choiceId);
                existsChoicesMap.put(choiceId, count);
            }
        }

        if (existsChoicesMap.isEmpty()) {
            return;
        }

        voteBatchProcessor.updateChoices(existsChoicesMap);
        voteBatchProcessor.updateTopic(existsChoicesMap, existsChoices);
        voteBatchProcessor.insertVoteRecords(existsChoicesMap, timeProvider.getCurrentTime());

    }

}
