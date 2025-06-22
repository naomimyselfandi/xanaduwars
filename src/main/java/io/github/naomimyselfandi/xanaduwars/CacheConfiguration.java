package io.github.naomimyselfandi.xanaduwars;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
class CacheConfiguration {

    @Bean
    SimpleCacheManager cacheManager() {
        var configuration = Caffeine.newBuilder().maximumSize(1000).build();
        var configCache = new CaffeineCache("configCache", configuration);
        var requests = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(1000).build();
        var requestCache = new CaffeineCache("requestCache", requests);
        var manager = new SimpleCacheManager();
        manager.setCaches(List.of(configCache, requestCache));
        return manager;
    }

}
