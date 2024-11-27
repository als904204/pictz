package online.pictz.api.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopicCountResponse {
    private Long id;
    private int totalCount;
}