package online.pictz.api.vote.service.memory;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.common.util.time.TimeProvider;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicStatus;
import online.pictz.api.topic.repository.TopicRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private InMemoryChoiceStorage choiceStorage;

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


}