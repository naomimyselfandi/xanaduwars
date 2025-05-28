package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
class DataSourceImpl implements DataSource {

    private static final String EXTENSION = "json";
    private static final String MANIFEST = "manifest";
    private static final String PARENT = "parent";

    // Using our own cache instead of @Cacheable for cleaner cycle detection.
    private final Map<Version, RulesetData> cache = new HashMap<>();
    private final Set<Version> blackhole = new HashSet<>();

    @Value("${xanadu.core.ruleset.root}")
    private final String root;
    private final ObjectMapper objectMapper;

    @Locked
    @Override
    public RulesetData load(Version version) throws IOException {
        return load0(version);
    }

    private RulesetData load0(Version version) throws IOException {
        if (!blackhole.add(version)) {
            throw new IOException("Cycle detected for ruleset %s.".formatted(version));
        }
        var cached = cache.get(version);
        if (cached != null) return cached;
        var loaded = load1(version);
        cache.put(version, loaded);
        blackhole.remove(version);
        return loaded;
    }

    private RulesetData load1(Version version) throws IOException {
        var prefix = "%s/%s".formatted(root, version);
        var suffix = ".%s".formatted(EXTENSION);
        // ClassGraph guarantees forward slashes, regardless of operating system.
        var pattern = Pattern.compile("%s/(.*)%s".formatted(prefix, suffix));
        try (var scan = new ClassGraph().acceptPaths(prefix).scan()) {
            log.info("Loading version {}.", version);
            var content = load(pattern, scan);
            var parent = content.remove(PARENT);
            var manifestJson = content.remove(MANIFEST);
            if (manifestJson == null) {
                var format = "Version %s is missing %s.";
                throw new IOException(format.formatted(version, withExtension(MANIFEST)));
            }
            var manifest = objectMapper.readValue(manifestJson, Manifest.class);
            var manifestFiles = manifest.toSet();
            ensureManifestListsAllFiles(version, manifestFiles, content);
            if (parent != null) {
                log.info("Version {} specifies parent {}.", version, parent);
                load0(objectMapper.readValue(parent, Version.class)).content().forEach(content::putIfAbsent);
            }
            ensureAllListedFilesExist(version, manifestFiles, content);
            content.keySet().retainAll(manifestFiles);
            log.info("Done loading version {}.", version);
            return new RulesetData(manifest, content);
        }
    }

    private Map<String, String> load(Pattern pattern, ScanResult scan) throws IOException {
        var resources = scan.getResourcesWithExtension(EXTENSION);
        var content = new HashMap<String, String>(resources.size());
        for (var resource : resources) {
            var path = resource.getPath();
            var matcher = pattern.matcher(path);
            var _ = matcher.matches();
            content.put(matcher.group(1), resource.getContentAsString());
        }
        return content;
    }

    private static void ensureManifestListsAllFiles(
            Version version,
            Set<String> manifestFiles,
            Map<String, ?> content
    ) throws IOException {
        var missing = content
                .keySet()
                .stream()
                .filter(Predicate.not(manifestFiles::contains))
                .map(DataSourceImpl::withExtension)
                .reduce("%s and %s"::formatted);
        if (missing.isPresent()) {
            throw new IOException("Version %s is missing manifest entry for %s.".formatted(version, missing.get()));
        }
    }

    private static void ensureAllListedFilesExist(
            Version version,
            Set<String> manifestFiles,
            Map<String, ?> content
    ) throws IOException {
        var missing = manifestFiles
                .stream()
                .filter(Predicate.not(content::containsKey))
                .map(DataSourceImpl::withExtension)
                .reduce("%s and %s"::formatted);
        if (missing.isPresent()) {
            throw new IOException("Version %s is missing %s.".formatted(version, missing.get()));
        }
    }

    @Override
    @Cacheable("configCache")
    public SortedSet<Version> scan() {
        try (var scan = new ClassGraph().acceptPaths(root).scan()) {
            log.info("Scanning {} for available versions...", root);
            // ClassGraph guarantees forward slashes, regardless of operating system.
            var pattern = Pattern.compile("%s/(.*)/%s\\.%s".formatted(root, MANIFEST, EXTENSION));
            var result = scan
                    .getResourcesWithLeafName(withExtension(MANIFEST))
                    .stream()
                    .map(resource -> {
                        var path = resource.getPath();
                        var matcher = pattern.matcher(path);
                        var _ = matcher.matches();
                        return new Version(matcher.group(1));
                    })
                    .toList();
            log.info("Done scanning {} for available versions (found {}).", root, result.size());
            return Collections.unmodifiableSortedSet(new TreeSet<>(result));
        }
    }

    private static String withExtension(String filename) {
        return "%s.%s".formatted(filename, EXTENSION);
    }

}
