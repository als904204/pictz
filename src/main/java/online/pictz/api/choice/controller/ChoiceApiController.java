package online.pictz.api.choice.controller;

import lombok.RequiredArgsConstructor;
import online.pictz.api.choice.dto.ChoiceVoteResult;
import online.pictz.api.choice.service.ChoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/choices")
@RequiredArgsConstructor
@RestController
public class ChoiceApiController {

    private final ChoiceService choiceService;

    /**
     *
     * @param id 조회할 Choice의 투표수 ID
     * @return Choice의 이름과 투표 수를 포함한 결과
     */
    @GetMapping("/{id}/vote-count")
    public ResponseEntity<ChoiceVoteResult> getChoiceVoteResultById(@PathVariable Long id) {
        ChoiceVoteResult choiceVoteResult = choiceService.getChoiceVoteResultById(id);
        return ResponseEntity.ok(choiceVoteResult);
    }

}
