package online.pictz.api.topic.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import online.pictz.api.config.TestConfig;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.topic.dto.TopicCountResponse;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.entity.TopicSort;
import online.pictz.api.topic.entity.TopicStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * 통합 테스트
 */
@Transactional
@Import(TestConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class TopicRepositoryIntegrationTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    EntityManager entityManager;

    private TestTimeProvider timeProvider = new TestTimeProvider(
        LocalDateTime.of(2024, 1, 1, 12, 0));


    @BeforeEach
    void setUp() {
        Topic topic1 = Topic.builder()
            .suggestedTopicId(1L)
            .title("토픽1")
            .slug("slug1")
            .status(TopicStatus.ACTIVE)
            .thumbnailImageUrl("url1")
            .createdAt(timeProvider.getCurrentTime())
            .build();

        Topic topic2 = Topic.builder()
            .suggestedTopicId(2L)
            .title("토픽2")
            .slug("slug2")
            .status(TopicStatus.ACTIVE)
            .thumbnailImageUrl("url2")
            .createdAt(timeProvider.getCurrentTime())
            .build();

        Topic topic3 = Topic.builder()
            .suggestedTopicId(3L)
            .title("토픽3")
            .slug("slug3")
            .status(TopicStatus.INACTIVE)
            .thumbnailImageUrl("url3")
            .createdAt(timeProvider.getCurrentTime())
            .build();

        topicRepository.saveAll(List.of(topic1, topic2, topic3));
    }

    @DisplayName("슬러그로 토픽을 찾을 수 있다")
    @Test
    void findBySlug() {

        // given
        String slug = "slug1";

        // when
        Optional<Topic> result = topicRepository.findBySlug(slug);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("토픽1");
    }
    
    @DisplayName("토픽 문의 ID로 토픽을 찾을 수 있다")
    @Test
    void findBySuggestedTopicId() {

        // given
        Long suggestId = 1L;

        // when
        Optional<Topic> result = topicRepository.findBySuggestedTopicId(suggestId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("토픽1");
    }

    @DisplayName("토픽 총 투표수가 업데이트 반영 되어야 한다")
    @Test
    void incrementTotalCount() {

        // given
        Topic topic = Topic.builder()
            .suggestedTopicId(999L)
            .title("토픽999")
            .slug("slug999")
            .status(TopicStatus.ACTIVE)
            .thumbnailImageUrl("url999")
            .createdAt(timeProvider.getCurrentTime())
            .build();

        topicRepository.save(topic);

        Long topicId = topic.getId();
        int increment = 500;

        // when
        int rowResult = topicRepository.incrementTotalCount(topicId, increment);

        // then
        assertThat(rowResult).isEqualTo(1);

        // jpa 1차 캐시  인한 업데이트 된 값 조회가 아닌 캐시 조회함. 그래서 영속성 강제 초기화
        entityManager.clear();

        Topic updatedTopic = topicRepository.findById(topicId).orElseThrow();
        assertThat(updatedTopic.getTotalCount()).isEqualTo(500);
    }

    @DisplayName("정렬 조건에 따른 활성화 상태 토픽만 조회해야한다")
    @Test
    void findActiveTopics() {

        // given
        TopicSort popular = TopicSort.POPULAR;
        int currentPage = 0;

        // when
        Page<TopicResponse> result = topicRepository.findActiveTopics(popular, currentPage);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();

        // 인기순 정렬 되었는지 확인
        assertThat(
            result.getContent().get(0).getTotalCount())
            .isGreaterThanOrEqualTo(result.getContent().get(0).getTotalCount()
        );

    }

    @DisplayName("페이지에 해당하는 토픽 목록 총 투표수를 조회한다")
    @Test
    void getTopicTotalCounts() {

        // given
        int currentPage = 0;

        // when
        List<TopicCountResponse> result = topicRepository.getTopicTotalCounts(
            currentPage);

        // then
        assertThat(result)
            .satisfies(responses -> {
                assertThat(responses.get(0).getId()).isEqualTo(1L);
                assertThat(responses.get(0).getTotalCount()).isEqualTo(0);
                assertThat(responses.get(1).getId()).isEqualTo(2L);
                assertThat(responses.get(1).getTotalCount()).isEqualTo(0);
            });
    }

    @DisplayName("토픽 제목으로 검색한다")
    @Test
    void searchTopics() {

        // given
        String query = "토픽1";
        TopicSort popular = TopicSort.POPULAR;
        int currentPage = 0;

        // when
        Page<TopicResponse> result = topicRepository.searchTopics(query, popular,
            currentPage);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("토픽1");
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }


}