package io.github.naomimyselfandi.xanaduwars.core.actions.ability;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.*;
import io.github.naomimyselfandi.xanaduwars.core.filter.Filters;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Name;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.Percent;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.TagSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RepairAbilityTest {

    private static final double AMOUNT = 0.2;

    @Mock
    private UnitType type;

    @Mock
    private Unit target, user;

    private RepairAbility fixture;

    @BeforeEach
    void setup(SeededRandom random) {
        var name = new Name("A" + random.nextInt(255));
        fixture = new RepairAbility(name, TagSet.EMPTY, Filters.defaultBiFilter(), new Percent(AMOUNT));
        lenient().when(target.type()).thenReturn(type);
    }

    @ParameterizedTest
    @MethodSource // Not a good candidate for CsvSource due to floating point inaccuracy
    void execute(double before, double after) {
        when(target.hp()).thenReturn(new Percent(before));
        assertThat(fixture.execute(user, target)).isEqualTo(Execution.SUCCESSFUL);
        verify(target).hp();
        verify(target).hp(new Percent(after));
        verifyNoMoreInteractions(target, user);
    }

    private static Stream<Arguments> execute() {
        return Stream.of( // Writing out the values directly is tricky due to floating point inaccuracy
                arguments(0.1, 0.1 + AMOUNT),
                arguments(0.7, 0.7 + AMOUNT),
                arguments(0.8, 1.0),
                arguments(0.9, 1.0)
        );
    }

    @MethodSource
    @ParameterizedTest
    void cost(Resource resource, int baseCost, double hp, int expected) {
        when(type.costs()).thenReturn(Map.of(resource, baseCost));
        when(target.hp()).thenReturn(new Percent(hp));
        assertThat(fixture.cost(resource, user, target)).isEqualTo(expected);
    }

    private static Stream<Arguments> cost() {
        return Stream.of(
                arguments(Resource.SUPPLIES, 100, 0.5, 20),
                arguments(Resource.SUPPLIES, 100, 0.9, 10),
                arguments(Resource.AETHER, 150, 0.5, 30)
        );
    }

}
