package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Version;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
class RulesetServiceImpl implements RulesetService {

    @Value("${xanadu.core.ruleset.root}")
    private final String root;

    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "configCache", unless = "#result == null")
    public @Nullable @Valid Ruleset load(Version version) {
        try (var scan = new ClassGraph().acceptPaths(root).scan()) {
            log.info("Loading ruleset for version {}.", version);
            var json = scan.getResourcesWithLeafName("%s.json".formatted(version)).stream().findFirst();
            return json.isEmpty() ? null : objectMapper.readValue(json.get().load(), Ruleset.class);
        } catch (Exception e) {
            log.error("Failed loading ruleset for version {}.", version, e);
            throw new RuntimeException("Failed loading ruleset for version %s.".formatted(version), e);
        }
    }

}
