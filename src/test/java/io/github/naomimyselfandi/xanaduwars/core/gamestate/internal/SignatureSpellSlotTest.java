package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.Bitset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SignatureSpellSlotTest {

    @Mock
    private Spell spell;

    private Bitset activation;

    private int index;

    private SignatureSpellSlot fixture;

    @BeforeEach
    void setup(SeededRng random) {
        activation = new Bitset();
        index = random.nextInt(16);
        fixture = new SignatureSpellSlot(spell, activation, index);
    }

    @Test
    void getSpell() {
        assertThat(fixture.getSpell()).contains(spell);
    }

    @Test
    void isRevealed() {
        assertThat(fixture.isRevealed()).isTrue();
    }

    @Test
    void reveal() {
        assertThat(fixture.reveal()).isSameAs(fixture);
        assertThat(fixture.isRevealed()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isActive(boolean value) {
        activation.set(index, value);
        assertThat(fixture.isActive()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setActive(boolean value) {
        assertThat(fixture.setActive(value)).isSameAs(fixture);
        assertThat(fixture.isActive()).isEqualTo(value);
        assertThat(activation.test(index)).isEqualTo(value);
    }

}
