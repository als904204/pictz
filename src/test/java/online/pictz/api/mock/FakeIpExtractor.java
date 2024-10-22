package online.pictz.api.mock;

import online.pictz.api.common.util.network.IpExtractor;

public class FakeIpExtractor implements IpExtractor {

    private final String fixedIp;

    public FakeIpExtractor(String fixedIp) {
        this.fixedIp = fixedIp;
    }

    @Override
    public String extractIp() {
        return fixedIp;
    }

}
