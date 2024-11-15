package online.pictz.api.choice.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import online.pictz.api.choice.dto.ChoiceCountResponse;
import online.pictz.api.choice.entity.Choice;
import online.pictz.api.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Import(TestConfig.class)
@ActiveProfiles("test")
@DataJpaTest
class ChoiceRepositoryIntegrationTest {

    @Autowired
    private ChoiceRepository choiceRepository;

    @BeforeEach
    void setUp() {
        Choice choice1 = new Choice(1L, "choice1", "url1", 1L);
        Choice choice2 = new Choice(1L, "choice2", "url2", 2L);
        choiceRepository.saveAll(List.of(choice1, choice2));
    }

    @Test
    void getChoiceTotalCounts() {

        // given
        List<Long> topicIds = List.of(1L);

        // when
        List<ChoiceCountResponse> result = choiceRepository.getChoiceTotalCounts(
            topicIds);

        // then
        assertThat(result)
            .hasSize(1)
            .satisfies(results -> {
                assertThat(results.get(0).getChoiceId()).isEqualTo(1L);
                assertThat(results.get(0).getVoteCount()).isEqualTo(0);
            });

    }

}