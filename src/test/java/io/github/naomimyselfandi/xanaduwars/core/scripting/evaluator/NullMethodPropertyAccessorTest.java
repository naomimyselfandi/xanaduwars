package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.LogicalSource;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class NullMethodPropertyAccessorTest {

    @Mock
    private MethodExecutor methodExecutor;

    @Mock
    private EvaluationContext context;

    @Mock
    private MethodResolver methodResolver;

    @InjectMocks
    private NullMethodPropertyAccessor fixture;

    private SeededRng random;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
    }

    @ParameterizedTest
    @LogicalSource(LogicalSource.Op.AND)
    void canRead(boolean targetExists, boolean nullaryMethodExists, boolean expected) throws AccessException {
        var target = targetExists ? new Object() : null;
        var name = random.nextString();
        if (targetExists && nullaryMethodExists) {
            when(methodResolver.resolve(context, target, name, List.of())).thenReturn(methodExecutor);
        }
        assertThat(fixture.canRead(context, target, name)).isEqualTo(expected);
        if (!targetExists) verifyNoInteractions(methodResolver);
    }

    @Test
    void read() throws AccessException {
        var target = new Object();
        var name = random.nextString();
        var value = new Object();
        when(methodResolver.resolve(context, target, name, List.of())).thenReturn(methodExecutor);
        when(methodExecutor.execute(context, target)).thenReturn(new TypedValue(value));
        assertThat(fixture.read(context, target, name)).isEqualTo(new TypedValue(value));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void read_WhenTheTargetIsNull_ThenThrows() {
        var name = random.nextString();
        assertThatThrownBy(() -> fixture.read(context, null, name)).isInstanceOf(AccessException.class);
        verifyNoInteractions(methodResolver);
    }

    @Test
    void read_WhenTheMethodDoesNotExist_ThenThrows() throws AccessException {
        var target = new Object();
        var name = random.nextString();
        when(methodResolver.resolve(context, target, name, List.of())).thenReturn(null);
        assertThatThrownBy(() -> fixture.read(context, target, name)).isInstanceOf(AccessException.class);
    }

    @Test
    void canWrite() {
        var target = new Object();
        var name = random.nextString();
        assertThat(fixture.canWrite(context, target, name)).isFalse();
        verifyNoInteractions(methodResolver);
    }

    @Test
    void write() {
        var target = new Object();
        var name = random.nextString();
        var value = new Object();
        assertThatThrownBy(() -> fixture.write(context, target, name, value)).isInstanceOf(AccessException.class);
        verifyNoInteractions(methodResolver);
    }

    @Test
    void getSpecificTargetClasses() {
        assertThat(fixture.getSpecificTargetClasses()).isEmpty();
    }

}
