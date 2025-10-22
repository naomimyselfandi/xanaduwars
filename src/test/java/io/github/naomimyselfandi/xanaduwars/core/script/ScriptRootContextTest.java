package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ScriptRootContextTest {

    @Mock
    private ScriptRuntime parent;

    @InjectMocks
    private ScriptRootContext fixture;

    @Test
    void lookupMissingVariable(SeededRng random) {
        var name = random.nextString();
        var value = random.nextString();
        when(parent.lookup(name)).thenReturn(value);
        assertThat(fixture.lookupMissingVariable(name)).isEqualTo(value);
    }

    @Test
    void lookupMissingVariable_WhenTheVariableIsUndefined_ThenReturnsUndefined(SeededRng random) {
        var name = random.nextString();
        when(parent.lookup(name)).thenReturn(null);
        assertThat(fixture.lookupMissingVariable(name)).isEqualTo(Undefined.UNDEFINED);
    }

    @Test
    void lookupMissingVariable_WhenTheVariableNameIsADollarSign_ThenReturnsTheRuntime() {
        assertThat(fixture.lookupMissingVariable("$")).isEqualTo(parent);
    }

}
