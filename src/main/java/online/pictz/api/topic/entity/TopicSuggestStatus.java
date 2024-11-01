package online.pictz.api.topic.entity;

import lombok.Getter;

@Getter
public enum TopicSuggestStatus {
    PENDING("보류중"),
    APPROVED("승인됨"),
    REJECTED("거부됨");

    private final String korean;

    TopicSuggestStatus(String korean) {
        this.korean = korean;
    }
}
