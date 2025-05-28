package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.gameplay.VersionService;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
class RulesetServiceImpl implements RulesetService {

    private final DataSource dataSource;
    private final RulesetFactory rulesetFactory;

    @Override
    @Cacheable(value = "configCache", unless = "#result == null")
    public Optional<Ruleset> load(Version version) {
        log.info("Loading ruleset for version {}.", version);
        try {
            if (dataSource.scan().contains(version)) {
                return Optional.of(rulesetFactory.load(version, dataSource.load(version)));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed loading version %s.".formatted(version), e);
        }
    }

}
