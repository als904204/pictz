package online.pictz.api.vote.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.choice.repository.ChoiceRepository;
import online.pictz.api.mock.TestIpExtractor;
import online.pictz.api.mock.TestTimeProvider;
import online.pictz.api.vote.dto.VoteRequest;
import online.pictz.api.vote.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 통합 테스트
 */
@ActiveProfiles("test")
@SpringBootTest
class VoteServiceImplIntegrationTest {

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private VoteRepository voteRepository;

    private VoteServiceImpl voteService;

    @BeforeEach
    void setUp() {

        voteService = new VoteServiceImpl(
            voteRepository,
            choiceRepository,
            new TestIpExtractor("1.1.1.1"),
            new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 0, 0, 0)),
            new VoteConverter(),
            new VoteValidator()
        );

        Choice choice1 = new Choice(1L, "메시", "메시 이미지");
        Choice choice2 = new Choice(1L, "호날두", "호날두 이미지");
        choiceRepository.saveAll(List.of(choice1, choice2));
    }

    @DisplayName("동시에 다수가 투표하더라도 모두 업데이트 되어야 한다")
    @Test
    void voteBulk() throws InterruptedException {

        // 50명의 유저가 있다고 가정
        int userCount = 50;

        // ThreadPool 크기
        int threadPoolSize = 32;
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(userCount);

        // given
        int tenVotes = 10;
        int twoVotes = 20;

        Long messiChoiceId = 1L;
        Long ronaldoChoiceId = 2L;

        List<VoteRequest> request = List.of(
            new VoteRequest(messiChoiceId, tenVotes),
            new VoteRequest(ronaldoChoiceId, twoVotes)
        );

        for (int i = 0; i < userCount; i++) {
            executorService.submit(() -> {
                try {
                    voteService.voteBulk(request);
                } catch (Exception e) {
                    // 예외 로그 기록
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        int expectedVoteCountForMessi =
            userCount * tenVotes;    // 실시간 투표 유저 수 * 투표한 count 수 (50 * 10 = 500)
        int expectedVoteCountForRonaldo =
            userCount * twoVotes;   // 실시간 투표 유저 수 * 투표한 count 수 (50 * 4 = 1000)

        Choice messiChoice = choiceRepository.findById(messiChoiceId).orElseThrow();
        Choice ronaldoChoice = choiceRepository.findById(ronaldoChoiceId).orElseThrow();

        assertThat(messiChoice.getVoteCount()).isEqualTo(expectedVoteCountForMessi);
        assertThat(ronaldoChoice.getVoteCount()).isEqualTo(expectedVoteCountForRonaldo);

        executorService.shutdown();
    }

}