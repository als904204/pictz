package online.pictz.api.topic.entity;

import lombok.Getter;

@Getter
public enum TopicStatus {

    ACTIVE("활성화"),
    INACTIVE("비활성화");

    private final String toKorea;

    TopicStatus(String toKorea) {
        this.toKorea = toKorea;
    }

}
