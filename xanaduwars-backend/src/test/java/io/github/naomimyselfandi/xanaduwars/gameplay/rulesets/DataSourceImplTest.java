package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class DataSourceImplTest {

    private static final @Language("json") String DETAILS = """
            {
              "moveAction": {"name": "Move"},
              "dropAction": {"name": "Drop"},
              "attackAction": {"name": "Attack"},
              "waitAction": {"name": "Wait"},
              "deployAction": {"name": "Deploy"},
              "passAction": {"name": "Pass"},
              "resignAction": {"name": "Resign"}
            }""";

    private final DataSourceImpl fixture = new DataSourceImpl("dataSourceTest", new ObjectMapper());

    @Test
    void load() throws IOException {
        var manifest = new Manifest(
                List.of("Foo", "Bar"),
                List.of("Alice", "Bob"),
                List.of("HocusPocus", "Abracadabra", "Alakazam"),
                List.of("Plains", "Road"),
                List.of("RifleSquad", "Vanguard")
        );
        var content = Stream.of(
                manifest.globalRules(),
                manifest.commanders(),
                manifest.spellTypes(),
                manifest.tileTypes(),
                manifest.unitTypes()
        ).flatMap(DataSourceImplTest::entries).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (_, _) -> fail(),
                HashMap::new
        ));
        content.put("details", DETAILS);
        var expected = new RulesetData(manifest, content);
        assertThat(fixture.load(new Version("1.0.0"))).isEqualTo(expected);
    }

    @Test
    void load_Parent() throws IOException {
        assertThat(load("1.0.1")).isEqualTo(load("1.0.0"));
    }

    @Test
    void load_ParentWithChanges() throws IOException {
        var manifest = new Manifest(
                List.of("Foo", "Bar"),
                List.of("Alice", "Bob"),
                List.of("Hocus", "Pocus", "Abracadabra", "Alakazam"),
                List.of("Plains", "Road", "Ocean"),
                List.of("RifleSquad", "Vanguard")
        );
        var content = Stream.of(
                manifest.globalRules(),
                manifest.commanders(),
                manifest.spellTypes(),
                manifest.tileTypes(),
                manifest.unitTypes()
        ).flatMap(DataSourceImplTest::entries).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (_, _) -> fail(),
                HashMap::new
        ));
        content.put("details", DETAILS);
        content.put("Alice", testContent("AliceLiddell"));
        var expected = new RulesetData(manifest, content);
        var actual = fixture.load(new Version("1.0.2"));
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1.0.0-CycleA,Cycle detected for ruleset %s.
            1.0.0-MissingEntry,Version %s is missing manifest entry for Vanguard.json.
            1.0.0-MissingFile,Version %s is missing Abracadabra.json and details.json.
            1.0.0-MissingManifest,Version %s is missing manifest.json.
            """)
    void load_Validation(String version, String message) {
        assertThatThrownBy(() -> load(version))
                .isInstanceOf(IOException.class)
                .hasMessage(message, version);
    }

    @Test
    void scan() {
        assertThat(fixture.scan()).containsExactly(
                new Version("1.0.0"),
                new Version("1.0.0-CycleA"),
                new Version("1.0.0-CycleB"),
                new Version("1.0.0-CycleC"),
                new Version("1.0.0-MissingEntry"),
                new Version("1.0.0-MissingFile"),
                new Version("1.0.1"),
                new Version("1.0.2")
        );
    }

    private static String testContent(String key) {
        return "{\"test\": \"%s\"}".formatted(key);
    }

    private static Stream<Map.Entry<String, String>> entries(List<String> keys) {
        return keys.stream().map(key -> Map.entry(key, testContent(key)));
    }

    private RulesetData load(String version) throws IOException {
        return fixture.load(new Version(version));
    }

}
