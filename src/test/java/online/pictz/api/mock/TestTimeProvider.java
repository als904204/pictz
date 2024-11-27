package online.pictz.api.mock;

import java.time.LocalDateTime;
import online.pictz.api.common.util.time.TimeProvider;

public class TestTimeProvider implements TimeProvider {

    private final LocalDateTime fixedTime;

    public TestTimeProvider(LocalDateTime fixedTime) {
        this.fixedTime = fixedTime;
    }

    @Override
    public LocalDateTime getCurrentTime() {
        return fixedTime;
    }
}
