package online.pictz.api.topic.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.service.ChoiceService;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@RestController
public class TopicApiController {

    private final TopicService topicService;
    private final ChoiceService choiceService;

    @GetMapping
    public ResponseEntity<List<TopicResponse>> findAll() {
        List<TopicResponse> responseList = topicService.findAll();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<TopicResponse> findBySlug(@PathVariable String slug) {
        TopicResponse response = topicService.findBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{topicId}/choices")
    public ResponseEntity<List<ChoiceResponse>> getChoicesForTopic(@PathVariable Long topicId) {
        List<ChoiceResponse> choices = choiceService.getChoiceListByTopicId(topicId);
        return ResponseEntity.ok(choices);
    }

}
