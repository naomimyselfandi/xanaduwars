package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.messages.PreflightQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AbilityDeclarationTest {

    @Mock
    private Ability ability, spell;

    @Mock
    private GameState gameState;

    @Mock
    private Player player;

    @Mock
    private Unit unit;

    private Actor actor;

    private Object target;

    private Map<String, Object> arguments;

    private String name;

    @Mock
    private Target<Object> targetSpec;

    @Mock
    private Script supplyCost, aetherCost, focusCost, filter, effect;

    private int supplyCostValue, aetherCostValue, focusCostValue;

    private final AbilityDeclaration fixture = new AbilityDeclaration() {

        @Override
        @NotNull Map<String, Object> makeArgumentMap(@NotNull Actor actor, @NotNull Object target) {
            assertThat(actor).isEqualTo(AbilityDeclarationTest.this.actor);
            assertThat(target).isEqualTo(AbilityDeclarationTest.this.target);
            return arguments;
        }

    };

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        actor = unit;
        this.random = random;
        target = new Object();
        arguments = Map.of(random.nextString(), new Object());
        name = random.nextString();
        fixture.setName(name);
        fixture.setTarget(targetSpec);
        fixture.setSupplyCost(supplyCost);
        fixture.setAetherCost(aetherCost);
        fixture.setFocusCost(focusCost);
        fixture.setFilter(filter);
        fixture.setEffect(effect);
        when(unit.getGameState()).thenReturn(gameState);
        when(unit.asPlayer()).thenReturn(player);
        supplyCostValue = random.nextInt(100, 1000);
        when(supplyCost.<Integer>executeNotNull(gameState, arguments)).thenReturn(supplyCostValue);
        aetherCostValue = random.nextInt(100, 1000);
        when(aetherCost.<Integer>executeNotNull(gameState, arguments)).thenReturn(aetherCostValue);
        focusCostValue = random.nextInt(100, 1000);
        when(focusCost.<Integer>executeNotNull(gameState, arguments)).thenReturn(focusCostValue);
    }

    @Test
    void unpack(SeededRng random) throws CommandException {
        var jsonNode = random.<JsonNode>get();
        when(targetSpec.unpack(unit, jsonNode)).thenReturn(target);
        assertThat(fixture.unpack(unit, jsonNode)).isEqualTo(target);
    }

    @Test
    void unpack_WhenTheInputIsInvalid_ThenThrows(SeededRng random) {
        var jsonNode = random.<JsonNode>get();
        when(targetSpec.unpack(unit, jsonNode)).thenReturn(null);
        assertThatThrownBy(() -> fixture.unpack(unit, jsonNode))
                .isInstanceOf(CommandException.class)
                .hasMessage("Malformed target '%s' for '%s'.", jsonNode, name);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            100,100,100
            0,0,0
            """)
    void validate(int supplySurplus, int aetherSurplus, int focusSurplus) {
        when(gameState.evaluate(new PreflightQuery(unit, fixture))).thenReturn(true);
        when(targetSpec.validate(unit, target)).thenReturn(true);
        when(filter.<Boolean>executeNotNull(gameState, arguments)).thenReturn(true);
        when(player.getSupplies()).thenReturn(supplyCostValue + supplySurplus);
        when(player.getAether()).thenReturn(aetherCostValue + aetherSurplus);
        when(player.getFocus()).thenReturn(focusCostValue + focusSurplus);
        assertThatCode(() -> fixture.validate(unit, target)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1,0,0,Insufficient supplies.
            100,0,0,Insufficient supplies.
            0,1,0,Insufficient aether.
            0,100,0,Insufficient aether.
            0,0,1,Insufficient focus.
            0,0,100,Insufficient focus.
            """)
    void validate_WhenThePlayerIsLackingAResource_ThenThrows(
            int supplyDeficit,
            int aetherDeficit,
            int focusDeficit,
            String message
    ) {
        when(gameState.evaluate(new PreflightQuery(unit, fixture))).thenReturn(true);
        when(targetSpec.validate(unit, target)).thenReturn(true);
        when(filter.<Boolean>executeNotNull(gameState, arguments)).thenReturn(true);
        when(player.getSupplies()).thenReturn(supplyCostValue - supplyDeficit);
        when(player.getAether()).thenReturn(aetherCostValue - aetherDeficit);
        when(player.getFocus()).thenReturn(focusCostValue - focusDeficit);
        assertThatThrownBy(() -> fixture.validate(unit, target))
                .isExactlyInstanceOf(CommandException.class)
                .hasMessage(message);
    }

    @Test
    void validate_WhenScriptedValidationFails_ThenThrows() {
        when(gameState.evaluate(new PreflightQuery(unit, fixture))).thenReturn(true);
        when(targetSpec.validate(unit, target)).thenReturn(true);
        when(filter.<Boolean>executeNotNull(gameState, arguments)).thenReturn(false);
        assertThatThrownBy(() -> fixture.validate(unit, target))
                .isExactlyInstanceOf(CommandException.class)
                .hasMessage("Invalid target for '%s' (in scripted validation).", name);
    }

    @Test
    void validate_WhenTargetValidationFails_ThenThrows() {
        when(gameState.evaluate(new PreflightQuery(unit, fixture))).thenReturn(true);
        when(targetSpec.validate(unit, target)).thenReturn(false);
        assertThatThrownBy(() -> fixture.validate(unit, target))
                .isExactlyInstanceOf(CommandException.class)
                .hasMessage("Invalid target for '%s' (in target specification).", name);
    }

    @Test
    void validate_WhenPreflightCheckFails_ThenThrows() {
        var jsonNode = random.<JsonNode>get();
        when(gameState.evaluate(new PreflightQuery(unit, fixture))).thenReturn(false);
        assertThatThrownBy(() -> fixture.validate(unit, jsonNode))
                .isExactlyInstanceOf(CommandException.class)
                .hasMessage("Can't use '%s' right now.", name);
    }

    @Test
    void getCost() {
        var expected = new Ability.Cost(supplyCostValue, aetherCostValue, focusCostValue);
        assertThat(fixture.getCost(unit, target)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true
            false,false
            ,true
            """)
    void execute(@Nullable Boolean scriptResult, boolean expected) {
        var supplies = random.nextInt(1000, 2000);
        when(player.getSupplies()).thenReturn(supplies);
        var aether = random.nextInt(1000, 2000);
        when(player.getAether()).thenReturn(aether);
        var focus = random.nextInt(1000, 2000);
        when(player.getFocus()).thenReturn(focus);
        when(unit.getActiveAbilities()).thenReturn(List.of(ability));
        when(effect.execute(gameState, arguments)).thenReturn(scriptResult);
        assertThat(fixture.execute(unit, target)).isEqualTo(expected);
        verify(player).setSupplies(supplies - supplyCostValue);
        verify(player).setAether(aether - aetherCostValue);
        verify(player).setFocus(focus - focusCostValue);
        verify(unit).setActiveAbilities(List.of(ability, fixture));
        verify(player, never()).setUsedAbilities(any());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,true
            false,false
            ,true
            """)
    void execute_Player(@Nullable Boolean scriptResult, boolean expected) {
        actor = player;
        when(player.getGameState()).thenReturn(gameState);
        when(player.asPlayer()).thenReturn(player);
        var supplies = random.nextInt(1000, 2000);
        when(player.getSupplies()).thenReturn(supplies);
        var aether = random.nextInt(1000, 2000);
        when(player.getAether()).thenReturn(aether);
        var focus = random.nextInt(1000, 2000);
        when(player.getFocus()).thenReturn(focus);
        when(player.getActiveAbilities()).thenReturn(List.of(ability));
        when(player.getUsedAbilities()).thenReturn(List.of(spell));
        when(effect.execute(gameState, arguments)).thenReturn(scriptResult);
        assertThat(fixture.execute(player, target)).isEqualTo(expected);
        verify(player).setSupplies(supplies - supplyCostValue);
        verify(player).setAether(aether - aetherCostValue);
        verify(player).setFocus(focus - focusCostValue);
        verify(player).setActiveAbilities(List.of(ability, fixture));
        verify(player).setUsedAbilities(List.of(spell, fixture));
    }

    @Test
    void propose(SeededRng random) {
        var jsonNode = random.<JsonNode>get();
        when(targetSpec.propose(unit)).thenReturn(Stream.of(target));
        when(targetSpec.pack(target)).thenReturn(jsonNode);
        when(filter.executeNotNull(gameState, arguments)).thenReturn(true);
        var cost = new Ability.Cost(supplyCostValue, aetherCostValue, focusCostValue);
        var expected = new AbilityDeclaration.Proposal(jsonNode, cost);
        assertThat(fixture.propose(actor)).containsExactly(expected);
    }

    @Test
    void propose_WhenATargetIsInvalid_ThenOmitsIt(SeededRng random) {
        var jsonNode = random.<JsonNode>get();
        when(targetSpec.propose(unit)).thenReturn(Stream.of(target));
        when(targetSpec.pack(target)).thenReturn(jsonNode);
        when(filter.executeNotNull(gameState, arguments)).thenReturn(false);
        assertThat(fixture.propose(actor)).isEmpty();
    }

}
