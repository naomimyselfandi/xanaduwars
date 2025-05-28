package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.ability;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Resource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter.Filters;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Percent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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
    private Unit target, subject;

    private RepairAbility fixture;

    @BeforeEach
    void setup(SeededRng random) {
        var name = random.nextName();
        fixture = new RepairAbility(name, Filters.defaultBiFilter(), new Percent(AMOUNT));
        lenient().when(target.type()).thenReturn(type);
    }

    @ParameterizedTest
    @MethodSource // Not a good candidate for CsvSource due to floating point inaccuracy
    void execute(double subjectHp, double before, double after) {
        when(subject.hp()).thenReturn(new Percent(subjectHp));
        when(target.hp()).thenReturn(new Percent(before));
        assertThat(fixture.execute(subject, target)).isEqualTo(Execution.SUCCESSFUL);
        verify(target).hp();
        verify(target).hp(new Percent(after));
        verifyNoMoreInteractions(target, subject);
    }

    private static Stream<Arguments> execute() {
        return Stream.of( // Writing out the values directly is tricky due to floating point inaccuracy
                arguments(1.0, 0.1, 0.1 + AMOUNT),
                arguments(0.5, 0.1, 0.1 + AMOUNT * 0.5),
                arguments(1.0, 0.7, 0.7 + AMOUNT),
                arguments(0.2, 0.7, 0.7 + AMOUNT * 0.2),
                arguments(1.0, 0.8, 1.0),
                arguments(1.0, 0.9, 1.0)
        );
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            SUPPLIES,100,1.0,0.5,20
            SUPPLIES,100,1.0,0.9,10
            AETHER,150,1.0,0.5,30
            AETHER,150,0.1,0.5,3
            """)
    void cost(Resource resource, int baseCost, double subjectHp, double targetHp, int expected) {
        when(subject.hp()).thenReturn(Percent.withDoubleValue(subjectHp));
        when(type.costs()).thenReturn(Map.of(resource, baseCost));
        when(target.hp()).thenReturn(new Percent(targetHp));
        assertThat(fixture.cost(resource, subject, target)).isEqualTo(expected);
    }

}
