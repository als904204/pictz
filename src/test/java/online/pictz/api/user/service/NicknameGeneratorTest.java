package online.pictz.api.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import online.pictz.api.common.util.random.SuffixGenerator;
import online.pictz.api.mock.TestNicknameApiClient;
import online.pictz.api.mock.TestSuffixGenerator;
import online.pictz.api.user.infrastructure.NicknameApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NicknameGeneratorTest {

    private NicknameGenerator nicknameGenerator;

    @BeforeEach
    void setUp() {
        NicknameApiClient nicknameApiClient = new TestNicknameApiClient("foo");
        SuffixGenerator suffixGenerator = new TestSuffixGenerator("#9999");

        nicknameGenerator = new NicknameGenerator(nicknameApiClient, suffixGenerator);
    }

    @Test
    void generateNickname() {

        // when
        String randomNickname = nicknameGenerator.generateNickname();

        // then
        assertThat(randomNickname).isEqualTo("foo#9999");
    }
}