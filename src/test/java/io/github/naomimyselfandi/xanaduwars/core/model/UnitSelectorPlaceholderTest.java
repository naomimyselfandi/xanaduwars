package io.github.naomimyselfandi.xanaduwars.core.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitSelectorPlaceholderTest {

    @Mock
    private UnitType unitType;

    @Mock
    private Unit unit;

    @Test
    void test_Unit() {
        assertThat(UnitSelectorPlaceholder.NONE.test(unit)).isFalse();
        verifyNoInteractions(unit);
    }

    @Test
    void test_UnitType() {
        assertThat(UnitSelectorPlaceholder.NONE.test(unitType)).isFalse();
        verifyNoInteractions(unitType);
    }

}
