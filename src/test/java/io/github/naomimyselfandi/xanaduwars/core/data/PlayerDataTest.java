package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerDataTest {

    private final PlayerData fixture = new PlayerData();

    @EnumSource
    @ParameterizedTest
    void resources(Resource resource) {
        var map = new EnumMap<>(Resource.class);
        for (var r : Resource.values()) {
            map.put(r, 0);
        }
        assertThat(fixture.resources()).isEqualTo(map);
        assertThat(fixture.resources(Map.of(resource, 123))).isSameAs(fixture);
        map.put(resource, 123);
        assertThat(fixture.resources()).isEqualTo(map);
    }

    @Test
    void knownSpells() {
        assertThat(fixture.knownSpells()).isEmpty();
        assertThat(fixture.knownSpells(List.of(1, 2, 3))).isSameAs(fixture);
        assertThat(fixture.knownSpells()).containsExactly(1, 2, 3);
    }

    @Test
    void activeSpells() {
        assertThat(fixture.activeSpells()).isEmpty();
        assertThat(fixture.activeSpells(List.of(1, 2, 3))).isSameAs(fixture);
        assertThat(fixture.activeSpells()).containsExactly(1, 2, 3);
    }

}
