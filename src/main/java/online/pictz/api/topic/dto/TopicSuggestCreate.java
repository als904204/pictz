package online.pictz.api.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class TopicSuggestCreate {
    private String title;
    private String description;
    private MultipartFile thumbnail;
}
