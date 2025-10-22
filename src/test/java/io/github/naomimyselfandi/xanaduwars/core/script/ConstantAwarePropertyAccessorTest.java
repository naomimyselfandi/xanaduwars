package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ConstantAwarePropertyAccessorTest {

    private interface Helper {
        @SuppressWarnings("unused") Helper setFoo(int foo);
    }

    private interface ConstantHelper extends Helper, ScriptConstant {}

    @Mock
    private EvaluationContext context;

    private ConstantAwarePropertyAccessor fixture;

    @BeforeEach
    void setup() {
        fixture = new ConstantAwarePropertyAccessor();
    }

    @Test
    void findSetterForProperty() throws AccessException {
        assertThat(fixture.canWrite(context, mock(Helper.class), "foo")).isTrue();
        assertThat(fixture.canWrite(context, mock(ConstantHelper.class), "foo")).isFalse();
    }

}
