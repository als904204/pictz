package online.pictz.api.choice.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import online.pictz.api.topic.entity.TopicSuggestChoiceImage;
import online.pictz.api.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChoiceTest {

    Choice choice;
    Long topicId = 1L;
    String name = "messi";
    String url = "url";
    Long imageId = 1L;

    @BeforeEach
    void setUp() {
        choice = new Choice(topicId, name, url, imageId);
    }

    @Test
    void updateVoteCount() {

        // when
        choice.updateVoteCount(10);
        int result = choice.getVoteCount();

        // then
        assertThat(result).isEqualTo(10);
    }

    @Test
    void updateImageDetail() {

        // given
        String newImgUrl = "new image url";
        String newFileName = "new file name";

        // when
        choice.updateImageDetail(newImgUrl, newFileName);

        // then
        assertThat(choice.getImageUrl()).isEqualTo("new image url");
        assertThat(choice.getName()).isEqualTo("new file name");
    }

    @Test
    void createFrom() {

        // given
        List<TopicSuggestChoiceImage> images = List.of(
            new TopicSuggestChoiceImage("messi url", "messi file"),
            new TopicSuggestChoiceImage("ronaldo url", "ronaldo file")
        );

        // when
        List<Choice> result = Choice.createFrom(topicId, images);

        // then
        assertThat(result)
            .satisfies(results -> {
                assertThat(results.get(0).getImageUrl()).isEqualTo("messi url");
                assertThat(results.get(0).getName()).isEqualTo("messi file");
                assertThat(results.get(1).getImageUrl()).isEqualTo("ronaldo url");
                assertThat(results.get(1).getName()).isEqualTo("ronaldo file");
            });
    }

    @Test
    void updateFrom() {
        Choice choice1;
        Choice choice2;

        TopicSuggestChoiceImage image1;
        TopicSuggestChoiceImage image2;

        // given
        List<TopicSuggestChoiceImage> images = List.of(
            image1 = new TopicSuggestChoiceImage("new url1", "new file1"),
            image2 = new TopicSuggestChoiceImage("new url2", "new file2")
        );

        List<Choice> choices = List.of(
            choice1 = new Choice(11L, "choice1", "old url1", image1.getId()),
            choice2 = new Choice(22L, "choice2", "old url2", image2.getId())
        );

        TestUtils.setId(image1, 1L);
        TestUtils.setId(image2, 2L);
        TestUtils.setId(choice1, 1L);
        TestUtils.setId(choice2, 2L);

        // when
        Choice.updateFrom(choices, images);

        // then
        assertThat(choices)
            .satisfies(updatedChoices -> {
                assertThat(updatedChoices.get(0).getImageUrl()).isEqualTo("new url1");
                assertThat(updatedChoices.get(0).getName()).isEqualTo("new file1");
                assertThat(updatedChoices.get(1).getImageUrl()).isEqualTo("new url2");
                assertThat(updatedChoices.get(1).getName()).isEqualTo("new file2");
            });

    }

}