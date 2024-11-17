package online.pictz.api.mock;

import online.pictz.api.common.util.random.SuffixGenerator;

public class TestSuffixGenerator implements SuffixGenerator {

    private final String fixedSuffix;

    public TestSuffixGenerator(String fixedSuffix) {
        this.fixedSuffix = fixedSuffix;
    }

    @Override
    public String generateSuffix() {
        return fixedSuffix;
    }
}
