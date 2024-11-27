package online.pictz.api.admin.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class SlugGeneratorImpl implements SlugGenerator{

    private static final String CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SLUG_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SLUG_LENGTH; i++) {
            int index = random.nextInt(CHAR_SET.length());
            sb.append(CHAR_SET.charAt(index));
        }
        return sb.toString();
    }
}
