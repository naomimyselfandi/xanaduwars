package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.SpellTypeId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.IntStream;

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
        var ids = IntStream.of(1, 2, 3).mapToObj(SpellTypeId::new).toList();
        assertThat(fixture.knownSpells()).isEmpty();
        assertThat(fixture.knownSpells(ids)).isSameAs(fixture);
        assertThat(fixture.knownSpells()).containsExactlyElementsOf(ids);
    }

    @Test
    void activeSpells() {
        var ids = IntStream.of(1, 2, 3).mapToObj(SpellTypeId::new).toList();
        assertThat(fixture.activeSpells()).isEmpty();
        assertThat(fixture.activeSpells(ids)).isSameAs(fixture);
        assertThat(fixture.activeSpells()).containsExactlyElementsOf(ids);
    }

}
