package online.pictz.api.vote.entity;


import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "vote")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "choice_id", nullable = false)
    private Long choiceId;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    private int count;

    @Builder
    public Vote(Long choiceId, String ip, LocalDateTime votedAt, int count) {
        this.choiceId = choiceId;
        this.ip = ip;
        this.votedAt = votedAt;
        this.count = count;
    }

    protected Vote() {}

}