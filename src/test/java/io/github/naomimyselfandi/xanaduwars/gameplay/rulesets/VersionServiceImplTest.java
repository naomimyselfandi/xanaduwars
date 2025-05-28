package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VersionServiceImplTest {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private VersionServiceImpl fixture;

    @Test
    void current() {
        var v100 = new Version("1.0.0");
        var v123 = new Version("1.2.3");
        var v123bar = new Version("1.2.3-Bar");
        var v123foo = new Version("1.2.3-Foo");
        var versions = new TreeSet<>(List.of(v100, v123, v123bar, v123foo));
        when(dataSource.scan()).thenReturn(versions);
        assertThat(fixture.current()).isEqualTo(v123);
    }

    @Test
    void stream() {
        var v100 = new Version("1.0.0");
        var v123 = new Version("1.2.3");
        var v123bar = new Version("1.2.3-Bar");
        var v123foo = new Version("1.2.3-Foo");
        var versions = new TreeSet<>(List.of(v100, v123, v123bar, v123foo));
        when(dataSource.scan()).thenReturn(versions);
        assertThat(fixture.stream()).containsExactly(v123foo, v123bar, v123, v100);
    }

}
