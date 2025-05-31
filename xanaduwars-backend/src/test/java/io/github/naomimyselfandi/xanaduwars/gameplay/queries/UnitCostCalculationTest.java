package io.github.naomimyselfandi.xanaduwars.gameplay.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.Tile;
import io.github.naomimyselfandi.xanaduwars.gameplay.UnitType;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitCostCalculationTest {

    @Mock
    private Tile subject;

    @Mock
    private UnitType target;

    @EnumSource
    @ParameterizedTest
    void defaultValue(Resource resource, SeededRng random) {
        var costs = Arrays.stream(Resource.values()).collect(Collectors.toMap(
                Function.identity(),
                _ -> random.nextIntNotNegative()
        ));
        when(target.costs()).thenReturn(costs);
        assertThat(new UnitCostCalculation(subject, target, resource).defaultValue()).isEqualTo(costs.get(resource));
    }

}
