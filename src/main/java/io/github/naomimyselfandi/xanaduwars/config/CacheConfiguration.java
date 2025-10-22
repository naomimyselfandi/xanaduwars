package io.github.naomimyselfandi.xanaduwars.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@EnableCaching
@Configuration
class CacheConfiguration {

    @Bean
    SimpleCacheManager cacheManager() {
        var configuration = Caffeine.newBuilder().maximumSize(1000).build();
        var configCache = new CaffeineCache(Cache.CONFIGURATION, configuration);
        var requests = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(10)).maximumSize(1000).build();
        var requestCache = new CaffeineCache(Cache.REQUESTS, requests);
        var manager = new SimpleCacheManager();
        manager.setCaches(List.of(configCache, requestCache));
        return manager;
    }

}
