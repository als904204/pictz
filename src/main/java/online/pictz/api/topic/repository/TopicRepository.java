package online.pictz.api.topic.repository;

import java.util.List;
import java.util.Optional;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.repository.dsl.TopicRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {

    Optional<Topic> findBySlug(String slug);

    Optional<Topic> findBySuggestedTopicId(Long suggestedTopicId);

    boolean existsByTitle(String title);

    boolean existsBySlug(String slug);

    @Query(
        value = "SELECT * " +
            "FROM topic t " +
            "WHERE t.status = 'ACTIVE'",
        nativeQuery = true)
    List<Topic> findActiveTopics();

}
