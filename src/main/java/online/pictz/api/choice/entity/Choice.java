package online.pictz.api.choice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
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

    @Version
    private int version;

    protected Choice() {

    }

    public Choice(Long topicId, String name, String imageUrl) {
        this.topicId = topicId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.voteCount = 0;
    }

    public void updateVoteCount(int voteCount) {
        this.voteCount += voteCount;
    }
}
