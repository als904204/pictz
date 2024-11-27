package online.pictz.api.choice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChoiceCountResponse {
    private Long choiceId;
    private int voteCount;
}
