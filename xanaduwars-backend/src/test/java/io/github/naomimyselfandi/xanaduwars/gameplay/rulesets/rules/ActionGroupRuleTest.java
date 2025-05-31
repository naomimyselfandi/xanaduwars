package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Action;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionGroupValidation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ActionGroupRuleTest {

    @Mock
    private Action<?, ?> x, y, z;

    private final ActionGroupRule fixture = new ActionGroupRule(
            Set.of(new Name("Simple1"), new Name("Simple2")),
            Set.of(new Name("Free1"), new Name("Free2"), new Name("Free3"))
    );

    @AfterEach
    void fixCoverageReport() {
        var _ = fixture.simpleActions();
        var _ = fixture.freeActions();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            Simple1,Normal1,Free1,true
            Simple1,Free1,Free2,true
            Normal1,Free1,Free2,true
            Normal1,Simple1,Free1,true
            Free1,Free2,Free3,true
            Normal1,Simple1,Normal2,false
            Simple1,Normal1,Simple2,false
            Normal1,Normal2,Simple1,false
            Simple1,Simple2,Normal1,false
            """)
    void isValid(String action1, String action2, String action3, boolean expected) {
        when(x.name()).thenReturn(new Name(action1));
        when(y.name()).thenReturn(new Name(action2));
        when(z.name()).thenReturn(new Name(action3));
        assertThat(fixture.isValid(new ActionGroupValidation(List.of(x, y, z)))).isEqualTo(expected);
    }

}
