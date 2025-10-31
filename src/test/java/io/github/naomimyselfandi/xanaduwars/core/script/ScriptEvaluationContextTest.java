package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Player;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class ScriptEvaluationContextTest {

    @Mock
    private UnaryOperator<String> helper;

    @Mock
    private ScriptRuntime runtime;

    @Mock
    private EvaluationContext parent;

    private ScriptEvaluationContext<UnaryOperator<String>> fixture;

    @BeforeEach
    void setup() {
        fixture = new ScriptEvaluationContext<>(helper) {

            @Override
            @Nullable Object lookupMissingVariable(@NotNull String name) {
                return helper.apply(name);
            }

        };
    }

    @Test
    void getRootObject() {
        assertThat(fixture.getRootObject()).isEqualTo(new TypedValue(Undefined.UNDEFINED));
    }

    @Test
    void getPropertyAccessors() {
        assertThat(fixture.getPropertyAccessors()).isUnmodifiable().<Object>map(Object::getClass).containsExactly(
                LibraryAccessor.class,
                VariableAccessor.class,
                ConstantAwarePropertyAccessor.class,
                MapAccessor.class
        );
    }

    @Test
    void getConstructorResolvers() {
        assertThat(fixture.getConstructorResolvers())
                .isUnmodifiable()
                .singleElement()
                .isExactlyInstanceOf(ReflectiveConstructorResolver.class);
    }

    @Test
    void getTypeLocator() {
        assertThat(fixture.getTypeLocator())
                .isInstanceOf(StandardTypeLocator.class)
                .returns(String.class, it -> it.findType("String"))
                .returns(List.class, it -> it.findType("List"))
                .returns(java.util.function.Function.class, it -> it.findType("Function"))
                .returns(Stream.class, it -> it.findType("Stream"))
                .returns(Player.class, it -> it.findType("Player"));
    }

    @Test
    void getTypeComparator() {
        assertThat(fixture.getTypeComparator()).isExactlyInstanceOf(StandardTypeComparator.class);
    }

    @Test
    void getOperatorOverloader() {
        assertThat(fixture.getOperatorOverloader()).isExactlyInstanceOf(StandardOperatorOverloader.class);
    }

    @Test
    void lookupVariable(SeededRng random) {
        var name = random.nextString();
        var value = random.nextString();
        fixture.setVariable(name, value);
        assertThat(fixture.lookupVariable(name)).isEqualTo(value);
        verifyNoInteractions(parent);
    }

    @Test
    void lookupVariable_WhenTheVariableIsMissing_ThenChecksTheParent(SeededRng random) {
        var name = random.nextString();
        var value = random.nextString();
        when(helper.apply(name)).thenReturn(value);
        assertThat(fixture.lookupVariable(name)).isEqualTo(value);
    }

    @Test
    void setVariable_CannotReplaceTheRuntime() {
        assertThatThrownBy(() -> fixture.setVariable("$", new Object()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot assign to $.");
    }

    @Test
    void getMethodResolvers() {
        assertThat(fixture.getMethodResolvers()).isUnmodifiable().satisfies(resolvers -> {
            assertThat(resolvers.get(0)).isExactlyInstanceOf(ConstantAwareMethodResolver.class);
            assertThat(resolvers.get(1)).isExactlyInstanceOf(FlowMethodResolver.class);
            assertThat(resolvers.stream().skip(2).<MethodResolver>map(Function.identity(/* AssertJ API quirk*/)))
                    .containsExactlyElementsOf(() -> fixture
                            .getPropertyAccessors()
                            .stream()
                            .<MethodResolver>map(FunctionMethodResolver::new)
                            .iterator());
        });
    }

    @Test
    void getBeanResolver() {
        assertThat(fixture.getBeanResolver()).isInstanceOf(MethodReferenceBeanResolver.class);
    }

    @Test
    void getTypeConverter() {
        assertThat(fixture.getTypeConverter())
                .isInstanceOf(StandardTypeConverter.class)
                .returns(true, it -> it.convertValue(
                        "foo",
                        TypeDescriptor.forObject("foo"),
                        TypeDescriptor.valueOf(Boolean.class)
                ));
    }

}
