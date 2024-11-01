package online.pictz.api.user.service;

import lombok.RequiredArgsConstructor;
import online.pictz.api.common.util.random.SuffixGenerator;
import online.pictz.api.user.infrastructure.NicknameApiClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NicknameGenerator {

    private final NicknameApiClient nicknameApiClient;
    private final SuffixGenerator suffixGenerator;

    public String generateNickname() {
        return nicknameApiClient.fetchNickname() + suffixGenerator.generateSuffix();
    }

}
