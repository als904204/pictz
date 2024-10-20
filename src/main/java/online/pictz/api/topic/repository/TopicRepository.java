package online.pictz.api.topic.repository;

import java.util.Optional;
import online.pictz.api.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findBySlug(String slug);

}
