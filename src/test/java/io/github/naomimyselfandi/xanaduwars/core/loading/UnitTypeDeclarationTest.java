package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class UnitTypeDeclarationTest {

    @Mock
    private Unit unit;

    @Mock
    private UnitType anotherType;

    private UnitTypeDeclaration fixture;

    @BeforeEach
    void setup() {
        fixture = new UnitTypeDeclaration();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_Unit(boolean value) {
        when(unit.getType()).thenReturn(value ? fixture : anotherType);
        assertThat(fixture.test(unit)).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void test_UnitType(boolean value) {
        assertThat(fixture.test(value ? fixture : anotherType)).isEqualTo(value);
    }

}
