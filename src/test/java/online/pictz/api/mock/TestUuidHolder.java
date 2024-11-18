package online.pictz.api.mock;

import online.pictz.api.common.util.random.UuidHolder;

public class TestUuidHolder implements UuidHolder {

    private final String fixedUuid;

    public TestUuidHolder(String fixedUuid) {
        this.fixedUuid = fixedUuid;
    }

    @Override
    public String random() {
        return fixedUuid;
    }
}
