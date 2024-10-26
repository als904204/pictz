package online.pictz.api.choice.dto;

import lombok.Getter;
import online.pictz.api.choice.entity.Choice;

@Getter
public class ChoiceResponse {

    private final Long topicId;
    private final Long choiceId;
    private final String name;
    private final String imageUrl;
    private final int voteCount;

    public ChoiceResponse(Choice choice) {
        this.topicId = choice.getTopicId();
        this.name = choice.getName();
        this.imageUrl = choice.getImageUrl();
        this.voteCount = choice.getVoteCount();
        this.choiceId = choice.getId();
    }

}
