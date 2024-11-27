package online.pictz.api.mock;

import online.pictz.api.common.util.network.IpExtractor;

public class TestIpExtractor implements IpExtractor {

    private final String fixedIp;

    public TestIpExtractor(String fixedIp) {
        this.fixedIp = fixedIp;
    }

    @Override
    public String extractIp() {
        return fixedIp;
    }

}
