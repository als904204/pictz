package online.pictz.api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import online.pictz.api.topic.entity.TopicSuggestStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AdminTopicSuggestUpdateResponse {

    private TopicSuggestStatus status;
    private String message;
    private String reason;

}