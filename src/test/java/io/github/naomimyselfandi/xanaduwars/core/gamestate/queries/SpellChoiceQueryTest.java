package io.github.naomimyselfandi.xanaduwars.core.gamestate.queries;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SpellChoiceQueryTest {

    @Mock
    private Spell abracadabra, alakazam;

    @Mock
    private Commander commander;

    @Test
    void subject() {
        assertThat(new SpellChoiceQuery(commander, List.of(abracadabra, alakazam)).subject()).isNull();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            false,false,1,Must choose exactly 1 spells.
            false,false,2,
            false,false,3,Must choose exactly 3 spells.
            true,false,2,Cannot choose signature spells.
            false,true,2,Cannot choose signature spells.
            """)
    void defaultValue(boolean firstIsSignature, boolean secondIsSignature, int available, @Nullable String problem) {
        when(abracadabra.isSignatureSpell()).thenReturn(firstIsSignature);
        when(alakazam.isSignatureSpell()).thenReturn(secondIsSignature);
        when(commander.getChosenSpells()).thenReturn(available);
        var result = problem == null ? Result.okay() : Result.fail(problem);
        assertThat(new SpellChoiceQuery(commander, List.of(abracadabra, alakazam)).defaultValue()).isEqualTo(result);
    }

    @Test
    void defaultValue_ProhibitsRepeatedSelections() {
        when(commander.getChosenSpells()).thenReturn(3);
        assertThat(new SpellChoiceQuery(commander, List.of(abracadabra, alakazam, abracadabra)).defaultValue())
                .isEqualTo(Result.fail("Cannot choose the same spell more than once."));
    }

}
