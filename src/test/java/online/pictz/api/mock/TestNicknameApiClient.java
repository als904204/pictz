package online.pictz.api.mock;

import online.pictz.api.user.infrastructure.NicknameApiClient;

public class TestNicknameApiClient implements NicknameApiClient {

    private final String fixedNickname;

    public TestNicknameApiClient(String fixedNickname) {
        this.fixedNickname = fixedNickname;
    }

    @Override
    public String fetchNickname() {
        return fixedNickname;
    }
}
