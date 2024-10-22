package online.pictz.api.vote.mock;

import online.pictz.api.common.util.network.IpExtractor;

public class FakeIpExtractor implements IpExtractor {

    @Override
    public String extractIp() {
        return "1.1.1.1";
    }

}
