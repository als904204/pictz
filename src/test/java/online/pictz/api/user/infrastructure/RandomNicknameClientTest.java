package online.pictz.api.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import online.pictz.api.user.infrastructure.exception.NicknameClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class RandomNicknameClientTest {

    private RandomNicknameClient randomNicknameClient;

    @Mock
    private RestTemplate restTemplate;

    private String nicknameApiUrl = "http://randomNickname.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        randomNicknameClient = new RandomNicknameClient(restTemplate, nicknameApiUrl);
    }

    @DisplayName("외부 닉네임 API 호출 성공")
    @Test
    void fetchNickname() {
        // given
        String expectedNickname = "foo";
        NicknameResponse nicknameResponse = new NicknameResponse("success", "true",
            expectedNickname);

        ResponseEntity<NicknameResponse> responseEntity = new ResponseEntity<>(nicknameResponse,
            HttpStatus.OK);

        when(restTemplate.postForEntity(
            eq(nicknameApiUrl),
            any(HttpEntity.class),
            eq(NicknameResponse.class)
        )).thenReturn(responseEntity);

        // when
        String resultNickname = randomNicknameClient.fetchNickname();

        // then
        assertThat(resultNickname).isEqualTo(expectedNickname);
    }

    @DisplayName("외부 닉네임 API 호출 시, 실패 응답 예외 발생")
    @Test
    void fetchNickname_fail_by_api() {
        // given
        String expectedNickname = "foo";
        NicknameResponse nicknameResponse = new NicknameResponse("error", "false",
            expectedNickname);

        ResponseEntity<NicknameResponse> responseEntity = new ResponseEntity<>(nicknameResponse,
            HttpStatus.OK);

        when(restTemplate.postForEntity(
            eq(nicknameApiUrl),
            any(HttpEntity.class),
            eq(NicknameResponse.class)
        )).thenReturn(responseEntity);

        // when
        NicknameClientException exception = assertThrows(
            NicknameClientException.class, () -> randomNicknameClient.fetchNickname());

        // then
        assertThat(exception.getMessage()).isEqualTo("Failed to request nickname from API: error (error code: false)");
        assertThat(exception.getErrorCode()).isEqualTo("E_401");
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


}