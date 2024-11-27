package online.pictz.api.common.util.random.impl;

import java.util.UUID;
import online.pictz.api.common.util.random.UuidHolder;
import org.springframework.stereotype.Component;

@Component
public class SystemUuidHolder implements UuidHolder {

    @Override
    public String random() {
        return UUID.randomUUID().toString();
    }

}
