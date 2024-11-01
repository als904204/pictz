package online.pictz.api.topic.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.common.annotation.CurrentUser;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.service.TopicSuggestService;
import online.pictz.api.user.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/topic_suggests")
@RestController
public class TopicSuggestApiController {

    private final TopicSuggestService topicSuggestService;

    @PostMapping
    public ResponseEntity<TopicSuggestResponse> create(@CurrentUser UserDto userDto, @RequestBody
    TopicSuggestCreate topicSuggestCreate) {
        TopicSuggestResponse response = topicSuggestService.createSuggest(userDto.getId(),
            topicSuggestCreate);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TopicSuggestResponse>> getUserTopicSuggests(
        @CurrentUser UserDto userDto) {
        List<TopicSuggestResponse> response = topicSuggestService.getTopicSuggestListByUserId(
            userDto.getId());
        return ResponseEntity.ok(response);
    }

}
