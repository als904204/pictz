package online.pictz.api.vote.service;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Map;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.util.TestUtils;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.service.optimistic.VoteConverter;
import online.pictz.api.vote.service.optimistic.VoteProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class VoteProcessorTest {

    private VoteProcessor voteProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        VoteConverter voteConverter = new VoteConverter();
        voteProcessor = new VoteProcessor(voteConverter);
    }

    @DisplayName("선택지 투표 수 만큼 선택지 count 증가한다")
    @Test
    void updateChoiceCount() {

        // given
        Choice choice1;
        Long choiceId = 1L;
        Long topicId = 1L;
        String name = "choice1";
        String url = "url1";
        Long imgId = 1L;

        List<Choice> choiceList = List.of(choice1 = new Choice(topicId, name, url, imgId));
        TestUtils.setId(choice1, choiceId);

        int choiceVoteCount = 100;
        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, choiceVoteCount));

        // when
        List<Choice> updatedChoiceCount = voteProcessor.updateChoiceCount(choiceList, voteRequests);

        // then
        assertThat(updatedChoiceCount.get(0).getCount()).isEqualTo(100);
        assertThat(updatedChoiceCount.get(0).getName()).isEqualTo(name);
        assertThat(updatedChoiceCount.get(0).getTopicId()).isEqualTo(1L);

    }

    @DisplayName("토픽 ID에 해당하는 선택지 투표 수를 합계한만큼 토픽 totalCount 증가한다")
    @Test
    void calculateTopicVoteTotalCount() {

        // given
        Choice choice1;
        Long choiceId = 1L;
        Long topicId = 1L;
        String name = "choice1";
        String url = "url1";
        Long imgId = 1L;

        List<Choice> choiceList = List.of(choice1 = new Choice(topicId, name, url, imgId));
        TestUtils.setId(choice1, choiceId);

        int voteCount = 100;
        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, voteCount));

        // when
        Map<Long, Integer> topicVoteTotalMap = voteProcessor.calculateTopicVoteTotalCount(choiceList,
            voteRequests);

        // then
        assertThat(topicVoteTotalMap.get(topicId)).isEqualTo(voteCount);
        assertThat(topicVoteTotalMap.get(2L)).isNull();
    }
}