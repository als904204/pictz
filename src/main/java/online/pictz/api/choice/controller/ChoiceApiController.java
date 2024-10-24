package online.pictz.api.choice.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.dto.ChoiceVoteResult;
import online.pictz.api.choice.service.ChoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/choices")
@RequiredArgsConstructor
@RestController
public class ChoiceApiController {

    private final ChoiceService choiceService;

    /**
     * 선택지 투표 결과 조회
     */
    @GetMapping("/{id}/vote-count")
    public ResponseEntity<ChoiceVoteResult> getChoiceVoteResultById(@PathVariable Long id) {
        ChoiceVoteResult choiceVoteResult = choiceService.getChoiceVoteResultById(id);
        return ResponseEntity.ok(choiceVoteResult);
    }

    /**
     * 여러 토픽에 관한 선택지 목록 조회
     */
    @GetMapping("/by-topics")
    public ResponseEntity<List<ChoiceResponse>> getChoicesForTopics(@RequestParam("topicIds") List<Long> topicIds) {
        List<ChoiceResponse> responseList = choiceService.getChoiceListByTopicIds(topicIds);
        return ResponseEntity.ok(responseList);
    }

}
