package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RulesetServiceImplTest {

    @Mock
    private Ruleset ruleset;

    @Mock
    private DataSource dataSource;

    @Mock
    private RulesetFactory rulesetFactory;

    @InjectMocks
    private RulesetServiceImpl fixture;

    @Test
    void load(SeededRng random) throws IOException {
        var version = random.nextVersion();
        var data = new RulesetData(
                new Manifest(
                        List.of(random.nextUUID().toString()),
                        List.of(random.nextUUID().toString()),
                        List.of(random.nextUUID().toString()),
                        List.of(random.nextUUID().toString()),
                        List.of(random.nextUUID().toString())
                ),
                Map.of(random.nextUUID().toString(), random.nextUUID().toString())
        );
        when(dataSource.load(version)).thenReturn(data);
        when(rulesetFactory.load(version, data)).thenReturn(ruleset);
        when(dataSource.scan()).thenReturn(new TreeSet<>(List.of(version)));
        assertThat(fixture.load(version)).contains(ruleset);
    }

    @Test
    void load_WhenLoadingTheRulesetThrows_ThenWrapsTheException(SeededRng random) throws IOException {
        var cause = new IOException("my dog ate the file");
        var version = random.nextVersion();
        var data = new RulesetData(
                new Manifest(
                        List.of(random.nextUUID().toString()),
                        List.of(random.nextUUID().toString()),
                        List.of(random.nextUUID().toString()),
                        List.of(random.nextUUID().toString()),
                        List.of(random.nextUUID().toString())
                ),
                Map.of(random.nextUUID().toString(), random.nextUUID().toString())
        );
        when(dataSource.load(version)).thenReturn(data);
        when(rulesetFactory.load(version, data)).thenThrow(cause);
        when(dataSource.scan()).thenReturn(new TreeSet<>(List.of(version)));
        assertThatThrownBy(() -> fixture.load(version))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed loading version %s.", version)
                .hasCause(cause);
    }

    @Test
    void load_WhenTheVersionIsNotDefined_ThenReturnsNothing(SeededRng random) {
        var version = random.nextVersion();
        when(dataSource.scan()).thenReturn(new TreeSet<>(List.of(random.nextVersion())));
        assertThat(fixture.load(version)).isEmpty();
    }

}
