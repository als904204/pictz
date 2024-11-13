package online.pictz.api.topic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChoiceImageRequest {
    private Long id;
    private MultipartFile choiceImage;
}
