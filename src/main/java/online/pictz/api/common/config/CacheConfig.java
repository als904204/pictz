package online.pictz.api.common.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 인기순 토픽 캐시 설정
     *
     * @return Caffeine 빌더 설정
     */
    @Bean
    public Caffeine<Object, Object> popularTopicsCaffeineConfig() {
        return Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .recordStats();
    }


    /**
     * 캐시 매니저 설정
     *
     * @return CacheManager 빈
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
            new CaffeineCache("popularTopics", popularTopicsCaffeineConfig().build())
        ));
        return cacheManager;
    }
}
