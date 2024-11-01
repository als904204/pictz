package online.pictz.api.common.util.random.impl;

import java.util.Random;
import online.pictz.api.common.util.random.SuffixGenerator;
import org.springframework.stereotype.Component;

@Component
public class RandomNumberSuffixGenerator implements SuffixGenerator {

    private final Random random = new Random();

    // 6자리 랜덤 숫자 생성
    @Override
    public String generateSuffix() {
        return "#" + String.format("%06d", random.nextInt(1000000));
    }

}
