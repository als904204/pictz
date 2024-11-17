package online.pictz.api.choice.entity;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;

@Getter
@Entity
@Table(name = "choice")
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "suggest_choice_image_id", nullable = false)
    private Long choiceImageId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "count", columnDefinition = "INT default 0")
    private int count;

    @Version
    private int version;

    protected Choice() {

    }

    public Choice(Long topicId, String name, String imageUrl, Long choiceImageId) {
        this.topicId = topicId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.choiceImageId = choiceImageId;
    }

    public void updateCount(int count) {
        this.count += count;
    }

    public void updateImageDetail(String imageUrl, String fileName) {
        this.imageUrl = imageUrl;
        this.name = fileName;
    }

    /**
     * topicId에 대한 새로운 선택지 생성
     * @param topicId 선택지의 부모 토픽
     * @param images  선택지의 이미지
     * @return 선택지
     */
    public static List<Choice> createFrom(Long topicId, List<TopicSuggestChoiceImage> images) {
        return images.stream()
            .map(image ->
                new Choice(topicId, image.getFileName(), image.getImageUrl(), image.getId()))
            .collect(Collectors.toList());
    }

    /**
     * 기존 선택지를 변경된 선택지 문의에 따라 이미지 정보 업데이트
     * @param choices 저장되어있는 선택지 목록
     * @param images  변경 요청온 선택지 목록 이미지 정보
     */
    public static void updateFrom(List<Choice> choices, List<TopicSuggestChoiceImage> images) {
        images.forEach(image -> choices.stream()
            .filter(choice -> choice.getId().equals(image.getId()))
            .findFirst()
            .ifPresent(choice -> choice.updateImageDetail(image.getImageUrl(), image.getFileName()))
        );
    }


}
