package online.pictz.api.topic.repository;

import java.util.Optional;
import online.pictz.api.topic.entity.Topic;
import online.pictz.api.topic.repository.dsl.TopicRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {

    Optional<Topic> findBySlug(String slug);

    Optional<Topic> findBySuggestedTopicId(Long suggestedTopicId);

}
