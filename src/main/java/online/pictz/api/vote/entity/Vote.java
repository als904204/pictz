package online.pictz.api.vote.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "vote")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "choice_id")
    private Long choiceId;

    @Column(name = "ip")
    private String ip;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Vote(Long choiceId, String ip, LocalDateTime createdAt) {
        this.choiceId = choiceId;
        this.ip = ip;
        this.createdAt = createdAt;
    }

    protected Vote() {}

}