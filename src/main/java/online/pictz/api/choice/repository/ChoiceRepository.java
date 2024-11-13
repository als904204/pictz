package online.pictz.api.choice.repository;

import java.util.List;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.dsl.ChoiceRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long>, ChoiceRepositoryCustom {

    List<Choice> findByTopicId(Long topicId);

    List<Choice> findByTopicIdIn(List<Long> topicIds);
}
