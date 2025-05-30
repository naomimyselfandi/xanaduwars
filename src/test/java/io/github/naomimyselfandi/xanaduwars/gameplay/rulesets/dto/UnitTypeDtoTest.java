package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitTypeDtoTest {

    @Mock
    private UnitType source, foo, bar;

    @Mock
    private Action<Unit, ?> action;

    @RepeatedTest(3)
    void mappings(SeededRng random) {
        var fooDamage = random.nextScalar();
        var barDamage = random.nextScalar();
        when(source.id()).thenReturn(random.nextUnitTypeId());
        when(source.name()).thenReturn(random.nextName());
        when(source.tags()).thenReturn(TagSet.of(random.nextTag(), random.nextTag()));
        when(source.costs()).thenReturn(Map.of(random.pick(Resource.values()), random.nextInt(1, 200)));
        when(source.vision()).thenReturn(random.nextInt(1, 10));
        when(source.speed()).thenReturn(random.nextInt(1, 10));
        when(source.range()).thenReturn(random.nextRange());
        when(source.damageTable()).thenReturn(Map.of(foo, fooDamage, bar, barDamage));
        when(foo.name()).thenReturn(random.nextName());
        when(bar.name()).thenReturn(random.nextName());
        when(source.hangar()).thenReturn(TagSet.of(random.nextTag(), random.nextTag()));
        when(source.abilities()).thenReturn(List.of(action));
        when(action.name()).thenReturn(random.nextName());
        var dto = new UnitTypeDto(source);
        assertThat(dto.getId()).isEqualTo(source.id());
        assertThat(dto.getName()).isEqualTo(source.name());
        assertThatCollection(dto.getTags()).isEqualTo(source.tags());
        assertThat(dto.getCosts()).isEqualTo(source.costs());
        assertThat(dto.getVision()).isEqualTo(source.vision());
        assertThat(dto.getSpeed()).isEqualTo(source.speed());
        assertThat(dto.getRange()).isEqualTo(source.range());
        assertThat(dto.getDamageTable())
                .hasSize(2)
                .containsEntry(foo.name(), fooDamage)
                .containsEntry(bar.name(), barDamage);
        assertThatCollection(dto.getHangar()).isEqualTo(source.hangar());
        assertThat(dto.getAbilities()).containsExactly(action.name());
    }

}
