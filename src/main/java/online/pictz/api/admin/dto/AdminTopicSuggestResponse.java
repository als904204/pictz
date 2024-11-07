package online.pictz.api.admin.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AdminTopicSuggestResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String nickname;
    private String status;
    private String rejectReason;
    private LocalDateTime createdAt;
    private List<String> choiceImageUrls;

    public AdminTopicSuggestResponse(Long id, String title, String nickname, String thumbnailUrl,
        String description, String status, String rejectReason, LocalDateTime createdAt,
        List<String> choiceImageUrls) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.choiceImageUrls = choiceImageUrls;
        this.nickname = nickname;
        this.description = description;
        this.status = status;
        this.rejectReason = rejectReason;
        this.createdAt = createdAt;
    }

}