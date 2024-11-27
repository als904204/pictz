package online.pictz.api.topic.entity;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "topic_suggest_choice_images")
@Getter
public class TopicSuggestChoiceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "fileName", nullable = false)
    private String fileName;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_suggest_id", nullable = false)
    private TopicSuggest topicSuggest;

    public TopicSuggestChoiceImage(String imageUrl, String fileName) {
        this.imageUrl = imageUrl;
        this.fileName = fileName;
    }

    /**
     * 이미지 정보 업데이트
     * @param newImageUrl 새로운 이미지 URL
     * @param newFileName 새로운 이미지 파일 이름
     */
    public void updateImageDetail(String newImageUrl, String newFileName) {
        this.imageUrl = newImageUrl;
        this.fileName = newFileName;
    }

    /**
     * 토픽문의 선택지 이미지를 ID 키로 하는 Map 반환
     * @param suggest 선택지 이미지를 포함한 토픽문의 객체
     * @return 선택지 이미지 ID를 키로 하는 Map
     */
    public static Map<Long, TopicSuggestChoiceImage> getImageIdMap(TopicSuggest suggest) {
        return suggest.getChoiceImages()
            .stream()
            .collect(Collectors.toMap(TopicSuggestChoiceImage::getId, Function.identity()));
    }

}
