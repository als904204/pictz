package online.pictz.api.choice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChoiceVoteResult {
    private final String name;
    private final int voteCount;
}
