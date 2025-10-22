package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class FunctionMethodResolverTest {

    @Mock
    private Function function;

    @Mock
    private EvaluationContext context;

    @Mock
    private PropertyAccessor propertyAccessor;

    private List<TypeDescriptor> argumentTypes;
    private String foo, bar, baz;

    @InjectMocks
    private FunctionMethodResolver fixture;

    @BeforeEach
    void setup(SeededRng random) {
        foo = random.nextString();
        bar = random.nextString();
        baz = random.nextString();
        lenient().when(function.call(foo, bar)).thenReturn(baz);
        argumentTypes = List.of(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(String.class));
    }

    @Test
    void resolve(SeededRng random) throws AccessException {
        var target = new Object();
        var name = random.nextString();
        when(propertyAccessor.canRead(context, target, name)).thenReturn(true);
        when(propertyAccessor.read(context, target, name)).thenReturn(new TypedValue(function));
        var executor = fixture.resolve(context, target, name, argumentTypes);
        assertThat(executor).isNotNull();
        assertThat(executor.execute(context, function, foo, bar)).isEqualTo(new TypedValue(baz));
    }

    @Test
    void resolve_WhenTheAccessorCannotReadAProperty_ThenNull(SeededRng random) throws AccessException {
        var target = new Object();
        var name = random.nextString();
        when(propertyAccessor.canRead(context, target, name)).thenReturn(false);
        assertThat(fixture.resolve(context, target, name, argumentTypes)).isNull();
        verify(propertyAccessor, never()).read(context, target, name);
    }

    @Test
    void resolve_WhenTheAccessorReturnsANonFunctionValue_ThenNull(SeededRng random) throws AccessException {
        var target = new Object();
        var name = random.nextString();
        when(propertyAccessor.canRead(context, target, name)).thenReturn(true);
        when(propertyAccessor.read(context, target, name)).thenReturn(new TypedValue(new Object()));
        assertThat(fixture.resolve(context, target, name, argumentTypes)).isNull();
    }

}
