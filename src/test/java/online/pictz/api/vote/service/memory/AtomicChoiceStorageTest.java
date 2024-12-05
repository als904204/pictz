package online.pictz.api.vote.service.memory;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import java.util.Map;
import online.pictz.api.vote.service.memory.atmoic.AtomicChoiceStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AtomicChoiceStorageTest {

    private AtomicChoiceStorage atomicChoiceStorage;

    @BeforeEach
    void setUp() {
        atomicChoiceStorage = new AtomicChoiceStorage();
    }

    @Test
    void store() {
        // given
        Long choiceId = 1L;
        int count = 10;

        // when
        atomicChoiceStorage.store(choiceId, count);

        // then
        int result = atomicChoiceStorage.getCount(choiceId);

        assertThat(result).isEqualTo(10);
    }


    @DisplayName("메모리에 있는 투표 값들을 정상적으로 가져와야 한다")
    @Test
    void getAndClearStorage() {
        // given
        Long choiceId = 1L;
        int count = 10;
        atomicChoiceStorage.store(choiceId, count);

        // when
        Map<Long, Integer> result = atomicChoiceStorage.getAndClearStorage();

        // then
        assertThat(result.get(choiceId)).isEqualTo(count);
        assertThat(result.containsKey(choiceId)).isTrue();
    }

    @DisplayName("메모리에 있는 투표 값들을 가져오면, 메모리에서 투표 값들이 삭제되어야한다")
    @Test
    void getAndClearStorageIsCleared() {
        // given
        Long choiceId = 1L;
        int count = 10;
        atomicChoiceStorage.store(choiceId, count);

        // when
        atomicChoiceStorage.getAndClearStorage();

        // then
        assertThat(atomicChoiceStorage.getCount(choiceId)).isEqualTo(0);
        assertThat(atomicChoiceStorage.getCount(choiceId)).isZero();
    }

    @Test
    void getCurrentCounts() {
        // given
        Long choiceId1 = 1L;
        int count1 = 10;

        Long choiceId2 = 2L;
        int count2 = 20;

        atomicChoiceStorage.store(choiceId1, count1);
        atomicChoiceStorage.store(choiceId2, count2);

        List<Long> choiceIds = List.of(choiceId1, choiceId2);

        // when
        Map<Long, Integer> result = atomicChoiceStorage.getCurrentCounts(choiceIds);

        // then
        Integer choice1Count = result.get(choiceId1);
        Integer choice2Count = result.get(choiceId2);

        assertThat(result).hasSize(2)
            .containsEntry(choiceId1, count1)
            .containsEntry(choiceId2, count2);

        assertThat(choice1Count).isEqualTo(count1);
        assertThat(choice2Count).isEqualTo(count2);
    }


}