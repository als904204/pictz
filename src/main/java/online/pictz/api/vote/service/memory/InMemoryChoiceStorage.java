package online.pictz.api.vote.service.memory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class InMemoryChoiceStorage {

    // 멀티 쓰레드 안전
    private final ConcurrentHashMap<Long, AtomicInteger> choiceStorage = new ConcurrentHashMap<>();

    /**
     * 메모리에 선택지 투표 임시 저장
     * @param choiceId 선택할 선택지 ID
     * @param count 투표 수
     */
    public void store(Long choiceId, int count) {
        if (count <= 0) {
            return;
        }
        // KEY 없다면 생성 후 VALUE : 0 설정
        AtomicInteger atomicInteger = choiceStorage.computeIfAbsent(choiceId,
            id -> new AtomicInteger(0));

        // 해당 KEY VALUE 증가
        atomicInteger.addAndGet(count);
    }

    /**
     * 메모리에 있는 선택지 리턴 후, 메모리 초기화
     * @return choiceId : count
     */
    public Map<Long, Integer> getAndClearStorage() {

        Map<Long, Integer> inMemoryStorage = new HashMap<>();

        for (Map.Entry<Long, AtomicInteger> entry : choiceStorage.entrySet()) {
            Long choiceId = entry.getKey();
            int count = entry.getValue().getAndSet(0);
            if (count > 0) {
                inMemoryStorage.put(choiceId, count);
            }
        }
        return inMemoryStorage;
    }

    /**
     * 특정 선택지들의 현재 투표 수를 반환
     * @param choiceIds 조회 할 선택지 ID
     * @return 요청된 선택지 ID와 해당 투표 수의 맵
     */
    public Map<Long, Integer> getCurrentCounts(List<Long> choiceIds) {
        Map<Long, Integer> currentCounts = new HashMap<>();
        for (Long choiceId : choiceIds) {
            AtomicInteger count = choiceStorage.get(choiceId);
            if (count != null) {
                currentCounts.put(choiceId, count.get());
            }
        }
        return currentCounts;
    }

    public int getCount(Long choiceId) {
        AtomicInteger atomicInteger = choiceStorage.get(choiceId);
        return atomicInteger != null ? atomicInteger.get() : 0;
    }

}
