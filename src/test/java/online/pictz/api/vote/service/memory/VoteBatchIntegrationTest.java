package online.pictz.api.vote.service.memory;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.repository.TopicRepository;
import online.pictz.api.vote.service.memory.atmoic.AtomicChoiceStorage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class VoteBatchIntegrationTest {

    @Autowired
    private VoteBatch voteBatch;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AtomicChoiceStorage choiceStorage;

    @Autowired
    private EntityManager entityManager;

    private final TimeProvider timeProvider = new TestTimeProvider(
        LocalDateTime.of(2024, 1, 1, 1, 1)
    );


    @BeforeEach
    void setup() {

        List<Choice> choices = List.of(
            new Choice(1L, "메시", "메시 이미지", 1L),
            new Choice(1L, "호날두", "호날두 이미지", 2L)
        );

        choiceRepository.saveAll(choices);

        Topic topic = Topic.builder()
            .title("메호대전")
            .thumbnailImageUrl("url")
            .status(TopicStatus.ACTIVE)
            .slug("hello")
            .createdAt(timeProvider.getCurrentTime())
            .build();

        topicRepository.save(topic);

    }

    @DisplayName("싱글 스레드 일 때 투표 Batch")
    @Test
    void batchWithSingleThread() {
        // given
        choiceStorage.store(1L, 10);
        choiceStorage.store(2L, 20);

        // when
        voteBatch.processBatchVotes();

        // 1차 캐시 때문에 DB 강제 반영
        entityManager.flush();
        entityManager.clear();

        // then
        Choice choice1 = choiceRepository.findById(1L).orElseThrow();
        Choice choice2 = choiceRepository.findById(2L).orElseThrow();
        Topic topic = topicRepository.findById(1L).orElseThrow();

        int choice1Count = choice1.getCount();
        int choice2Count = choice2.getCount();
        int topicTotalCount = topic.getTotalCount();

        Assertions.assertThat(choice1Count).isEqualTo(10);
        Assertions.assertThat(choice2Count).isEqualTo(20);
        Assertions.assertThat(topicTotalCount).isEqualTo(30);
    }

    @DisplayName("50명이 동시에 투표해도 집계가 정확해야 한다")
    @Test
    void batchWithMultiThread() throws InterruptedException, ExecutionException {

        // given
        int numberOfThreads = 50; // 스레드 수
        int votesForChoice1 = 10; // 선택지1에 대한 투표 수
        int votesForChoice2 = 20; // 선택지2에 대한 투표 수

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            tasks.add(() -> {
                choiceStorage.store(1L, votesForChoice1);
                choiceStorage.store(2L, votesForChoice2);
                return null;
            });
        }

        // 모든 스레드 작업 기다리기
        List<Future<Void>> futures = executorService.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get(); // 예외 발생 시 throw
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // when
        voteBatch.processBatchVotes();

        // 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        // then
        Choice choice1 = choiceRepository.findById(1L).orElseThrow();
        Choice choice2 = choiceRepository.findById(2L).orElseThrow();

        int choice1Count = choice1.getCount();
        int choice2Count = choice2.getCount();

        // 50명이 10번씩 choice1 투표 (50 * 10 = 500)
        int expectedChoice1Count = numberOfThreads * votesForChoice1;

        // 50명이 20번씩 choice2 투표 (50 * 20 = 1000)
        int expectedChoice2Count = numberOfThreads * votesForChoice2;

        assertThat(choice1Count).isEqualTo(expectedChoice1Count);
        assertThat(choice2Count).isEqualTo(expectedChoice2Count);

        Topic topic = topicRepository.findById(1L).orElseThrow();
        int topicTotalCount = topic.getTotalCount();

        // 토픽 총 투표 수
        int expectedTotalTopicCount = expectedChoice1Count + expectedChoice2Count;
        assertThat(topicTotalCount).isEqualTo(expectedTotalTopicCount);
    }


}