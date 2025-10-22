package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VersionServiceImplTest {

    @Mock
    private Version version;

    @Mock
    private VersionLoader versionLoader;

    private VersionServiceImpl fixture;

    @BeforeEach
    void setup() throws IOException {
        fixture = new VersionServiceImpl(versionLoader, "infraTestVersion");
    }

    @Test
    void getVersionNumbers() {
        var page = Pageable.ofSize(3);
        assertThat(fixture.getVersionNumbers(page)).isUnmodifiable().containsExactly(
                new VersionNumber(1, 2, 3, ""),
                new VersionNumber(1, 0, 1, ""),
                new VersionNumber(1, 0, 1, "bar")
        );
        assertThat(fixture.getVersionNumbers(page.next())).isUnmodifiable().containsExactly(
                new VersionNumber(1, 0, 1, "foo"),
                new VersionNumber(1, 0, 0, "")
        );
    }

    @Test
    void getPublishedVersionNumbers() {
        var page = Pageable.ofSize(2);
        assertThat(fixture.getPublishedVersionNumbers(page)).isUnmodifiable().containsExactly(
                new VersionNumber(1, 2, 3, ""),
                new VersionNumber(1, 0, 1, "")
        );
        assertThat(fixture.getPublishedVersionNumbers(page.next())).isUnmodifiable().containsExactly(
                new VersionNumber(1, 0, 0, "")
        );
    }

    @Test
    void getVersion() throws IOException {
        var versionNumber = new VersionNumber(1, 2, 3, "");
        when(versionLoader.load(versionNumber, "\"123\"")).thenReturn(version);
        assertThat(fixture.getVersion(versionNumber)).isEqualTo(version);
    }

    @Test
    void getVersion_WhenTheVersionNumberIsUnknown_ThenThrows(SeededRng random) {
        var versionNumber = random.<VersionNumber>get();
        assertThatThrownBy(() -> fixture.getVersion(versionNumber))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Unknown version '%s'.", versionNumber);
    }

    @Test
    void getVersion_WhenLoadingTheVersionFails_ThenThrows(SeededRng random) throws IOException {
        var exception = new IOException(random.nextString());
        var versionNumber = new VersionNumber(1, 2, 3, "");
        when(versionLoader.load(versionNumber, "\"123\"")).thenThrow(exception);
        assertThatThrownBy(() -> fixture.getVersion(versionNumber))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed loading version '%s'!", versionNumber)
                .hasCause(exception);
    }

}
