package online.pictz.api.vote.service.memory.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import online.pictz.api.vote.service.memory.InMemoryChoiceStorage;
import org.springframework.stereotype.Component;

/**
 * 동시성 문제가 발생
 */
@Deprecated
@Component
public class SyncChoiceStorageV0 implements InMemoryChoiceStorage {

    private final Map<Long, Integer> choiceStorage = new HashMap<>();

    /**
     * 메모리에 선택지 투표 임시 저장
     * @param choiceId 선택할 선택지 ID
     * @param count 투표 수
     */
    public void store(Long choiceId, int count) {
        if (count <= 0) {
            return;
        }

        Integer currentCount = choiceStorage.getOrDefault(choiceId, 0);

        choiceStorage.put(choiceId, currentCount + count);
    }

    public Map<Long, Integer> getAndClearStorage() {

        Map<Long, Integer> inMemoryStorage = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : choiceStorage.entrySet()) {
            Long choiceId = entry.getKey();
            int count = entry.getValue();
            if (count > 0) {
                inMemoryStorage.put(choiceId, count);
            }
            entry.setValue(0);
        }
        return inMemoryStorage;
    }

    @Override
    public Map<Long, Integer> getCurrentCounts(List<Long> choiceIds) {
        Map<Long, Integer> currentCounts = new HashMap<>();
        for (Long choiceId : choiceIds) {
            currentCounts.put(choiceId, choiceStorage.getOrDefault(choiceId, 0));
        }
        return currentCounts;
    }

    @Override
    public int getCount(Long choiceId) {
        return choiceStorage.getOrDefault(choiceId, 0);
    }

}
