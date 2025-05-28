package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.xanaduwars.gameplay.VersionService;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
class VersionServiceImpl implements VersionService {

    private final DataSource dataSource;

    @Override
    public Version current() {
        return stream().filter(Version::isPublished).findFirst().orElseThrow();
    }

    @Override
    public Stream<Version> stream() {
        return dataSource.scan().reversed().stream();
    }

}
