package online.pictz.api.choice.repository;

import java.util.List;
import online.pictz.api.choice.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    List<Choice> findByTopicId(Long topicId);

    long countById(Long id);

    List<Choice> findByTopicIdIn(List<Long> topicIds);
}
