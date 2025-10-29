package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitAbilityQueryTest {

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private Ability moveAbility, fireAbility, typeAbility, dropAbility;

    @Mock
    private Version version;

    @Mock
    private GameState gameState;

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit, cargo;

    private UnitAbilityQuery fixture;

    @BeforeEach
    void setup() {
        when(unitType.getAbilities()).thenReturn(List.of(typeAbility));
        when(version.getMoveAbility()).thenReturn(moveAbility);
        when(version.getFireAbility()).thenReturn(fireAbility);
        when(version.getDropAbility()).thenReturn(dropAbility);
        when(gameState.getVersion()).thenReturn(version);
        when(unit.getGameState()).thenReturn(gameState);
        when(unit.getType()).thenReturn(unitType);
        fixture = new UnitAbilityQuery(unit);
    }

    enum AbilityReference {MOVE, FIRE, TYPE, DROP}

    @MethodSource
    @ParameterizedTest
    void defaultValue(int speed, int weaponCount, boolean hasCargo, List<AbilityReference> expected, SeededRng random) {
        var weapons = IntStream.range(0, weaponCount).mapToObj(_ -> random.<Weapon>get()).toList();
        when(unit.getSpeed()).thenReturn(speed);
        when(unit.getWeapons()).thenReturn(weapons);
        when(unit.getUnit()).thenReturn(hasCargo ? cargo : null);
        var expectedAbilities = expected.stream().map(it -> switch (it) {
            case MOVE -> moveAbility;
            case FIRE -> fireAbility;
            case TYPE -> typeAbility;
            case DROP -> dropAbility;
        }).toList();
        assertThat(fixture.defaultValue(runtime)).isEqualTo(expectedAbilities);
        verifyNoInteractions(runtime);
    }

    private static Stream<Arguments> defaultValue() {
        var move = AbilityReference.MOVE;
        var fire = AbilityReference.FIRE;
        var type = AbilityReference.TYPE;
        var drop = AbilityReference.DROP;
        return Stream.of(
                arguments(0, 0, false, List.of(type)),
                arguments(1, 0, false, List.of(move, type)),
                arguments(2, 0, false, List.of(move, type)),
                arguments(0, 1, false, List.of(fire, type)),
                arguments(0, 2, false, List.of(fire, type)),
                arguments(0, 0, true, List.of(type, drop)),
                arguments(1, 1, false, List.of(move, fire, type)),
                arguments(1, 0, true, List.of(move, type, drop)),
                arguments(1, 1, true, List.of(move, fire, type, drop))
        );
    }

}
