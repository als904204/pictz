package online.pictz.api.topic.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class TopicSuggestRequest {
    private String title;
    private String description;
    private MultipartFile thumbnail;
    private List<MultipartFile> choiceImages;
}
