package online.pictz.api.topic.repository;

import java.util.List;
import online.pictz.api.topic.entity.TopicSuggest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicSuggestRepository extends JpaRepository<TopicSuggest, Long> {
    List<TopicSuggest> findByUserId(Long userId);

    boolean existsByTitle(String title);
}
