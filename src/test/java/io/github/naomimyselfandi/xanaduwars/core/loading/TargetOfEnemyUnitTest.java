package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetOfEnemyUnitTest {

    @Mock
    private Unit actor, target;

    @Mock
    private Target<Unit> base;

    @InjectMocks
    private TargetOfEnemyUnit fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void validateFurther(boolean value) {
        when(actor.isEnemy(target)).thenReturn(value);
        assertThat(fixture.validateFurther(actor, target)).isEqualTo(value);
    }

}
