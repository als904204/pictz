package online.pictz.api.choice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "choice")
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "vote_count", columnDefinition = "INT default 0")
    private int voteCount;

    protected Choice() {

    }

    public Choice(Long topicId, String name, String imageUrl) {
        this.topicId = topicId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.voteCount = 0;
    }

    public void incrementVoteCount() {
        this.voteCount += 1;
    }
}
