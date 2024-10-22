package online.pictz.api.vote.controller;

import lombok.RequiredArgsConstructor;
import online.pictz.api.vote.dto.VoteCreate;
import online.pictz.api.vote.dto.VoteResponse;
import online.pictz.api.vote.service.VoteService;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<VoteResponse> createVote(@RequestBody VoteCreate voteCreate) {
        VoteResponse voteResponse = voteService.vote(voteCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(voteResponse);
    }

}
