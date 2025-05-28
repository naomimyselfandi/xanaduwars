package io.github.naomimyselfandi.xanaduwars.gameplay;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StructureTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private TileType tileType, foundation;

    @InjectMocks
    private Structure.Validator fixture;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isValid(boolean expected) {
        when(tileType.foundation()).thenReturn(Optional.of(foundation).filter(_ -> expected));
        assertThat(fixture.isValid(tileType, context)).isEqualTo(expected);
    }

}
