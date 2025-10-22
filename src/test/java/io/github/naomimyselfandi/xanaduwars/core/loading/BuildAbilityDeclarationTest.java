package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Tile;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BuildAbilityDeclarationTest {

    @Mock
    private Unit actor;

    @Mock
    private Tile target;

    @Mock
    private UnitType unitType;

    @InjectMocks
    private BuildAbilityDeclaration fixture;

    @Test
    void makeArgumentMap() {
        var expected = Map.of("actor", actor, "target", target, "unitType", unitType);
        assertThat(fixture.makeArgumentMap(actor, target)).isEqualTo(expected);
    }

}
