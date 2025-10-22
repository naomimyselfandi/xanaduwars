package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.classgraph.ClassGraph;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;
import io.github.naomimyselfandi.xanaduwars.core.service.VersionService;
import io.github.naomimyselfandi.xanaduwars.util.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
class VersionServiceImpl implements VersionService {

    private final Map<VersionNumber, String> versionJson = new ConcurrentHashMap<>();

    private final @Unmodifiable List<VersionNumber> versionNumbers;
    private final @Unmodifiable List<VersionNumber> publishedVersionNumbers;

    private final VersionLoader versionLoader;

    VersionServiceImpl(VersionLoader versionLoader, @Value("${xanadu.game.root}") String root) throws IOException {
        this.versionLoader = versionLoader;
        var versionNumbers = new ArrayList<VersionNumber>();
        var publishedVersionNumbers = new ArrayList<VersionNumber>();
        try (var scan = new ClassGraph().acceptPaths(root).scan()) {
            for (var resource : scan.getResourcesWithExtension("json")) {
                var parts = resource.getPath().split("/");
                var rawVersionNumber = parts[parts.length - 1].replaceFirst("\\.json$", "");
                var versionNumber = VersionNumber.of(rawVersionNumber);
                versionNumbers.add(versionNumber);
                if (versionNumber.suffix().isEmpty()) {
                    publishedVersionNumbers.add(versionNumber);
                }
                versionJson.put(versionNumber, resource.getContentAsString());
            }
        }
        versionNumbers.sort(Comparator.reverseOrder());
        publishedVersionNumbers.sort(Comparator.reverseOrder());
        this.versionNumbers = List.copyOf(versionNumbers);
        this.publishedVersionNumbers = List.copyOf(publishedVersionNumbers);
    }

    @Override
    public @Unmodifiable List<VersionNumber> getVersionNumbers(Pageable page) {
        var min = (int) page.getOffset();
        var max = Math.min(versionNumbers.size(), min + page.getPageSize());
        return versionNumbers.subList(min, max);
    }

    @Override
    public @Unmodifiable List<VersionNumber> getPublishedVersionNumbers(Pageable page) {
        var min = (int) page.getOffset();
        var max = Math.min(publishedVersionNumbers.size(), min + page.getPageSize());
        return publishedVersionNumbers.subList(min, max);
    }

    @Override
    @Cacheable(value = "configCache")
    public Version getVersion(VersionNumber versionNumber) {
        if (!(versionJson.get(versionNumber) instanceof String json)) {
            throw new NotFoundException("Unknown version '%s'.".formatted(versionNumber));
        } else try {
            log.info("Loading version '{}'...", versionNumber);
            var version = versionLoader.load(versionNumber, json);
            log.info("Successfully loaded version '{}'.", versionNumber);
            return version;
        } catch (IOException | RuntimeException e) {
            var message = "Failed loading version '%s'!".formatted(versionNumber);
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

}
