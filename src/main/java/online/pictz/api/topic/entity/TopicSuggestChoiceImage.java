package online.pictz.api.topic.entity;

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
import lombok.Setter;

@Entity
@Table(name = "topic_suggest_choice_images")
@Getter
public class TopicSuggestChoiceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_suggest_id", nullable = false)
    private TopicSuggest topicSuggest;

    public TopicSuggestChoiceImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
