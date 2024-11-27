package online.pictz.api.topic.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class TopicSuggestUpdate {
    private String title;
    private String description;
    private MultipartFile thumbnail;
    private List<ChoiceImageRequest> choiceImages;
}
