package online.pictz.api.choice.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceResponse;
import online.pictz.api.choice.dto.ChoiceCountResponse;
import online.pictz.api.choice.service.ChoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/count")
    public ResponseEntity<List<ChoiceCountResponse>> getChoiceCounts(@RequestParam("choiceIds") List<Long> choiceIds) {
        List<ChoiceCountResponse> response = choiceService.getChoiceCounts(choiceIds);
        return ResponseEntity.ok(response);
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
