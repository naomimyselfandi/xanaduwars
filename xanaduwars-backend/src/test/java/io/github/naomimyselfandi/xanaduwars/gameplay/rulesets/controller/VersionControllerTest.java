package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.VersionService;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VersionControllerTest {

    private List<Version> versions;

    @Mock
    private VersionService versionService;

    @InjectMocks
    private VersionController fixture;

    @BeforeEach
    void setup(SeededRng random) {
        versions = IntStream
                .range(0, 5)
                .boxed()
                .flatMap(_ -> Stream.of(random.nextPublishedVersion(), random.nextInternalVersion()))
                .sorted()
                .toList();
        when(versionService.stream()).then(_ -> versions.stream());
    }

    @Test
    void get() {
        var expected = ResponseEntity.ok(versions.stream().filter(Version::isPublished).toList());
        assertThat(fixture.get(PageRequest.of(0, 20))).isEqualTo(expected);
    }

    @Test
    void get_SupportsPagination() {
        var publishedVersions = versions.stream().filter(Version::isPublished).toList();
        var expected = ResponseEntity.ok(publishedVersions.subList(2, 4));
        assertThat(fixture.get(PageRequest.of(1, 2))).isEqualTo(expected);
    }

    @Test
    void getAll() {
        var expected = ResponseEntity.ok(versions);
        assertThat(fixture.getAll(PageRequest.of(0, 20))).isEqualTo(expected);
    }

    @Test
    void getAll_SupportsPagination() {
        var expected = ResponseEntity.ok(versions.subList(6, 9));
        assertThat(fixture.getAll(PageRequest.of(2, 3))).isEqualTo(expected);
    }

}
