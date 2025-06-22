package io.github.naomimyselfandi.xanaduwars.core.ruleset.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.VersionService;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VersionControllerTest {

    private List<Version> versions;

    @Mock
    private Pageable pageable;

    @Mock
    private VersionService versionService;

    @InjectMocks
    private VersionController fixture;

    @BeforeEach
    void setup(SeededRng random) {
        versions = IntStream.range(0, 10).mapToObj(_ -> random.<Version>get()).toList();
    }

    @RepeatedTest(3)
    void get(SeededRng random) {
        var pageSize = random.nextInt(2, 4);
        var offset = pageSize * random.nextInt(0, 3);
        when(pageable.getPageSize()).thenReturn(pageSize);
        when(pageable.getOffset()).thenReturn((long) offset);
        when(versionService.published()).thenReturn(versions);
        assertThat(fixture.get(pageable)).isEqualTo(ResponseEntity.ok(versions.subList(offset, offset + pageSize)));
    }

    @RepeatedTest(3)
    void getAll(SeededRng random) {
        var pageSize = random.nextInt(2, 4);
        var offset = pageSize * random.nextInt(0, 3);
        when(pageable.getPageSize()).thenReturn(pageSize);
        when(pageable.getOffset()).thenReturn((long) offset);
        when(versionService.all()).thenReturn(versions);
        assertThat(fixture.getAll(pageable)).isEqualTo(ResponseEntity.ok(versions.subList(offset, offset + pageSize)));
    }

}
