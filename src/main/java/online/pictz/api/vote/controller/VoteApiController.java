package online.pictz.api.vote.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/votes")
@RestController
public class VoteApiController {

    private final VoteService voteService;

    /**
     * 투표 일괄 처리
     */
    @PostMapping("/bulk")
    public ResponseEntity<Void> voteBulk(@RequestBody List<VoteRequest> voteRequest) {
        voteService.voteBulk(voteRequest);
        return ResponseEntity.noContent().build();
    }

}
