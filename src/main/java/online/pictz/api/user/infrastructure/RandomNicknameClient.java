package online.pictz.api.user.infrastructure;

import online.pictz.api.user.infrastructure.exception.NicknameClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class RandomNicknameClient implements NicknameApiClient {

    private final RestTemplate restTemplate;
    private final String nicknameApiUrl;

    public RandomNicknameClient(RestTemplate restTemplate,
        @Value("${nickname.api.url}") String nicknameApiUrl) {
        this.restTemplate = restTemplate;
        this.nicknameApiUrl = nicknameApiUrl;
    }

    @Override
    public String fetchNickname() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("lang", "ko");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<NicknameResponse> response = restTemplate.postForEntity(
            nicknameApiUrl, request, NicknameResponse.class);

        NicknameResponse nicknameResponse = response.getBody();

        if (response.getStatusCode() == HttpStatus.OK
            && response.getBody() != null
            && "true".equalsIgnoreCase(nicknameResponse.getCode())) {
            return nicknameResponse.getData();
        } else {
            throw NicknameClientException.of(nicknameResponse.getMsg(), nicknameResponse.getCode());
        }
    }
}
