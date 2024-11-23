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
@RequestMapping("/api/optimistic/votes")
@RestController
public class VoteApiController {

    private final VoteService voteService;

    /**
     * 투표 일괄 처리
     * 멀티쓰레드 동시성 문제 및 요청마다 DB 쓰기로 인한 과부하 발생
     */
    @Deprecated
    @PostMapping
    public ResponseEntity<Void> voteBulk(@RequestBody List<VoteRequest> voteRequest) {
        voteService.voteBulk(voteRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/")
    public ResponseEntity<Void> voteBulkInMemory(@RequestBody List<VoteRequest> voteRequest) {
        voteService.voteBulk(voteRequest);
        return ResponseEntity.noContent().build();
    }

}
