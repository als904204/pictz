package online.pictz.api.topic.repository;

import java.util.Optional;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.repository.dsl.TopicRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {

    Optional<Topic> findBySlug(String slug);

    Optional<Topic> findBySuggestedTopicId(Long suggestedTopicId);

    @Modifying
    @Query("UPDATE Topic t "
        + " SET t.totalCount = t.totalCount + :increment "
        + " WHERE t.id = :topicId")
    int incrementTotalCount(@Param("topicId") Long topicId, @Param("increment") int increment);
}
