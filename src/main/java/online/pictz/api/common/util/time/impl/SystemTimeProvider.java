package online.pictz.api.common.util.time.impl;

import java.time.LocalDateTime;
import online.pictz.api.common.util.time.TimeProvider;
import org.springframework.stereotype.Component;

@Component
public class SystemTimeProvider implements TimeProvider {

    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

}
