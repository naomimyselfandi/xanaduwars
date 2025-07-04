package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.regex.Pattern;

@Slf4j
@Configuration
class VersionServiceConfiguration {

    @Bean
    VersionService versionService(@Value("${xanadu.core.ruleset.root}") String root) {
        log.info("Scanning {} for versions.", root);
        var pattern = Pattern.compile("%s/([^/]*)\\.json".formatted(root));
        try (var scan = new ClassGraph().acceptPaths(root).scan()) {
            var unsorted = scan
                    .getResourcesMatchingPattern(pattern)
                    .stream()
                    .map(Resource::getPath)
                    .peek(x -> log.info("Found version {}.", x))
                    .map(path -> {
                        var matcher = pattern.matcher(path);
                        var _ = matcher.matches(); // Guaranteed
                        return matcher.group(1);
                    })
                    .map(Version::new)
                    .peek(x -> log.info("Found matching version {}.", x))
                    .toList();
            var all = new TreeSet<>(Comparator.<Version>naturalOrder().reversed());
            all.addAll(unsorted);
            var published = all.stream().filter(Version::isPublished).toList();
            return new VersionServiceImpl(Collections.unmodifiableSequencedSet(all), published);
        }
    }

}
