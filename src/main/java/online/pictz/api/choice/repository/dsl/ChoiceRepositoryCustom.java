package online.pictz.api.choice.repository.dsl;

import java.util.List;
import online.pictz.api.choice.dto.ChoiceCountResponse;

public interface ChoiceRepositoryCustom {

    List<ChoiceCountResponse> getChoiceTotalCounts(List<Long> topicIds);

}
