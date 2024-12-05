package online.pictz.api.vote.service.memory;

import java.util.List;
import java.util.Map;

public interface InMemoryChoiceStorage {

    void store(Long choiceId, int count);

    Map<Long, Integer> getAndClearStorage();

    Map<Long, Integer> getCurrentCounts(List<Long> choiceIds);

    int getCount(Long choiceId);

}
