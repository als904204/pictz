package online.pictz.api.topic.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.common.annotation.CurrentUser;
import online.pictz.api.topic.dto.TopicSuggestCreate;
import online.pictz.api.topic.dto.TopicSuggestUpdate;
import online.pictz.api.topic.dto.TopicSuggestResponse;
import online.pictz.api.topic.service.TopicSuggestService;
import online.pictz.api.user.dto.UserDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/topic-suggests")
@RestController
public class TopicSuggestApiController {

    private final TopicSuggestService topicSuggestService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TopicSuggestResponse> create(@CurrentUser UserDto userDto, @ModelAttribute
    TopicSuggestCreate suggestRequest) {
        TopicSuggestResponse response = topicSuggestService.createSuggest(userDto.getId(),
            suggestRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TopicSuggestResponse>> getUserTopicSuggestList(
        @CurrentUser UserDto userDto) {
        List<TopicSuggestResponse> response = topicSuggestService.getUserTopicSuggestList(
            userDto.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicSuggestResponse> getUserTopicSuggestDetail(
        @PathVariable("id") Long id,
        @CurrentUser UserDto userDto) {
        TopicSuggestResponse response = topicSuggestService.getUserTopicSuggestDetail(id, userDto.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{topicSuggestId}")
    public ResponseEntity<TopicSuggestResponse> updateUserTopicSuggest(
        @PathVariable("topicSuggestId") Long topicSuggestId,
        @CurrentUser UserDto userDto,
        @ModelAttribute TopicSuggestUpdate updateDto) {
        TopicSuggestResponse response = topicSuggestService.updateTopicSuggest(topicSuggestId,
            userDto.getId(), updateDto);
        return ResponseEntity.ok(response);
    }


}
