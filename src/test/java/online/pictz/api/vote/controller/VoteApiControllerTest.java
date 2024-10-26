package online.pictz.api.vote.controller;

import online.pictz.api.vote.service.VoteService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteApiControllerTest {

    @Mock
    private VoteService voteService;

    @InjectMocks
    private VoteApiController voteApiController;


}