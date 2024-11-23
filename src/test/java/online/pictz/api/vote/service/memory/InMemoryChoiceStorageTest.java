package online.pictz.api.vote.service.memory;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryChoiceStorageTest {

    private InMemoryChoiceStorage inMemoryChoiceStorage;

    @BeforeEach
    void setUp() {
        inMemoryChoiceStorage = new InMemoryChoiceStorage();
    }

    @Test
    void store() {
        // given
        Long choiceId = 1L;
        int count = 10;

        // when
        inMemoryChoiceStorage.store(choiceId, count);

        // then
        int result = inMemoryChoiceStorage.getCount(choiceId);

        assertThat(result).isEqualTo(10);
    }


    @DisplayName("메모리에 있는 투표 값들을 정상적으로 가져와야 한다")
    @Test
    void getAndClearStorage() {
        // given
        Long choiceId = 1L;
        int count = 10;
        inMemoryChoiceStorage.store(choiceId, count);

        // when
        Map<Long, Integer> result = inMemoryChoiceStorage.getAndClearStorage();

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
        inMemoryChoiceStorage.store(choiceId, count);

        // when
        inMemoryChoiceStorage.getAndClearStorage();

        // then
        assertThat(inMemoryChoiceStorage.getCount(choiceId)).isEqualTo(0);
        assertThat(inMemoryChoiceStorage.getCount(choiceId)).isZero();
    }

    @Test
    void getCurrentCounts() {
        // given
        Long choiceId1 = 1L;
        int count1 = 10;

        Long choiceId2 = 2L;
        int count2 = 20;

        inMemoryChoiceStorage.store(choiceId1, count1);
        inMemoryChoiceStorage.store(choiceId2, count2);

        List<Long> choiceIds = List.of(choiceId1, choiceId2);

        // when
        Map<Long, Integer> result = inMemoryChoiceStorage.getCurrentCounts(choiceIds);

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