package online.pictz.api.common.util.network.impl;

import javax.servlet.http.HttpServletRequest;
import online.pictz.api.common.util.network.IpExtractor;
import org.springframework.stereotype.Component;

@Component
public class HttpServletIpExtractor implements IpExtractor {

    private final HttpServletRequest request;

    public HttpServletIpExtractor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String extractIp() {
        String header = request.getHeader("X-Forwarded-For");

        /**
         * proxy 없을 시
         */
        if (header == null || header.isEmpty()) {
            return request.getRemoteAddr();
        }

        /**
         * 있으면 0번째 ip 추출
         */
        return header.split(",")[0];
    }
}
