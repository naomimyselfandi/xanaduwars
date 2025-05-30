package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.TileType;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class TileTypeDtoTest {

    @Mock
    private TileType source, foundation;

    @Mock
    private UnitType foo, bar;

    @RepeatedTest(3)
    void mappings(SeededRng random) {
        var fooId = random.nextUnitTypeId();
        var barId = random.nextUnitTypeId();
        if (fooId.index() > barId.index()) {
            var swap = barId;
            barId = fooId;
            fooId = swap;
        }
        when(source.id()).thenReturn(random.nextTileTypeId());
        when(source.name()).thenReturn(random.nextName());
        when(source.tags()).thenReturn(TagSet.of(random.nextTag(), random.nextTag()));
        when(source.movementTable()).thenReturn(Map.of(random.nextTag(), random.nextDouble()));
        when(source.cover()).thenReturn(random.nextPercent());
        when(source.foundation()).thenReturn(Optional.of(foundation));
        when(foundation.id()).thenReturn(random.nextTileTypeId());
        when(source.costs()).thenReturn(Map.of(random.pick(Resource.values()), random.nextInt(1, 200)));
        when(source.income()).thenReturn(Map.of(random.pick(Resource.values()), random.nextInt(1, 200)));
        when(source.buildTime()).thenReturn(random.nextInt(1, 5));
        when(source.deploymentRoster()).thenReturn(Set.of(bar, foo));
        when(foo.id()).thenReturn(fooId);
        when(bar.id()).thenReturn(barId);
        var dto = new TileTypeDto(source);
        assertThat(dto.getId()).isEqualTo(source.id());
        assertThat(dto.getName()).isEqualTo(source.name());
        assertThatCollection(dto.getTags()).isEqualTo(source.tags());
        assertThat(dto.getMovementTable()).isEqualTo(source.movementTable());
        assertThat(dto.getCover()).isEqualTo(source.cover());
        assertThat(dto.getFoundation()).isEqualTo(foundation.id());
        assertThat(dto.getCosts()).isEqualTo(source.costs());
        assertThat(dto.getIncome()).isEqualTo(source.income());
        assertThat(dto.getBuildTime()).isEqualTo(source.buildTime());
        assertThat(dto.getDeploymentRoster()).containsExactly(fooId, barId);
    }

}
