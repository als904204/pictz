package online.pictz.api.vote.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.entity.Vote;
import online.pictz.api.vote.service.optimistic.VoteConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class VoteConverterTest {

    private VoteConverter voteConverter;
    private TestTimeProvider timeProvider = new TestTimeProvider(
        LocalDateTime.of(2024, 1, 1, 1, 1));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.voteConverter = new VoteConverter();
    }

    @DisplayName("투표 요청 정보에서 선택지 ID만 추출 한다")
    @Test
    void convertToChoiceIds() {

        // given
        Long choiceId = 1L;
        int count = 10;

        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, count));

        // when
        List<Long> choiceIds = voteConverter.convertToChoiceIds(voteRequests);

        // then
        assertThat(choiceIds).isNotNull();
        assertThat(choiceIds.get(0)).isEqualTo(choiceId);
    }

    @DisplayName("투표 정보 Dto to Entity 변환")
    @Test
    void convertToVoteEntities() {

        // given
        Long choiceId = 1L;
        int count = 10;

        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, count));

        // when
        List<Vote> votes = voteConverter.convertToVoteEntities(voteRequests, timeProvider.getCurrentTime());

        // then
        assertThat(votes.get(0).getChoiceId()).isEqualTo(choiceId);
        assertThat(votes.get(0).getCount()).isEqualTo(count);
    }

    @DisplayName("투표 정보 Map으로 변환")
    @Test
    void convertToVoteCountMap() {

        // given
        Long choiceId = 1L;
        int count = 10;

        List<VoteRequest> voteRequests = List.of(new VoteRequest(choiceId, count));

        // when
        Map<Long, Integer> resultMap = voteConverter.convertToVoteCountMap(voteRequests);

        // then
        assertThat(resultMap.get(choiceId)).isEqualTo(count);
    }
}