package io.github.naomimyselfandi.xanaduwars.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.support.SimpleCacheManager;

import static org.assertj.core.api.Assertions.*;

class CacheConfigurationTest {

    private final CacheConfiguration fixture = new CacheConfiguration();

    @Test
    void cacheManager() {
        var actual = fixture.cacheManager();
        assertThat(actual).isExactlyInstanceOf(SimpleCacheManager.class);
        actual.initializeCaches();
        assertThat(actual.getCacheNames()).containsOnly(Cache.CONFIGURATION, Cache.REQUESTS);
    }

}
