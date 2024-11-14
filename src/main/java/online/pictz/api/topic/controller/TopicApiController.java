package online.pictz.api.topic.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.service.ChoiceService;
import online.pictz.api.common.dto.PagedResponse;
import online.pictz.api.topic.dto.TopicCountResponse;
import online.pictz.api.topic.dto.TopicResponse;
import online.pictz.api.topic.entity.TopicSort;
import online.pictz.api.topic.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@RestController
public class TopicApiController {

    private final TopicService topicService;
    private final ChoiceService choiceService;

    /**
     * @param sortBy 최신순, 인기순
     * @param page   요청 페이지
     * @return       정렬된 Topic 목록
     */
    @GetMapping
    public ResponseEntity<PagedResponse<TopicResponse>> getActiveTopics(
        @RequestParam(defaultValue = "POPULAR") TopicSort sortBy,
        @RequestParam(defaultValue = "0") int page
    ) {
        PagedResponse<TopicResponse> response = topicService.getActiveTopics(sortBy, page);
        return ResponseEntity.ok(response);
    }

    /**
     * 슬러그를 이용한 토픽의 선택지 목록 조회
     */
    @GetMapping("/{slug}/choices")
    public ResponseEntity<List<ChoiceResponse>> getChoicesForTopicBySlug(@PathVariable("slug") String slug) {
        List<ChoiceResponse> response = choiceService.getChoiceListByTopicSlug(slug);
        return ResponseEntity.ok(response);
    }

    /**
     * 페이지에 해당하는 토픽 목록 총 투표수 조회
     * @param page 현재 페이지
     * @return 총 투표수
     */
    @GetMapping("/counts")
    public ResponseEntity<List<TopicCountResponse>> getTopicCounts(@RequestParam(defaultValue = "0") int page) {
        List<TopicCountResponse> counts = topicService.getAllTopicCounts(page);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<TopicResponse>> search(
        @RequestParam("q") String query,
        @RequestParam(defaultValue = "POPULAR") TopicSort sortBy,
        @RequestParam(defaultValue = "0") int page) {
        PagedResponse<TopicResponse> response = topicService.searchTopics(query, sortBy, page);
        return ResponseEntity.ok(response);
    }

}
