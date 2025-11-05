package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.springframework.hateoas.server.core.DummyInvocationUtils;
import org.springframework.hateoas.server.core.LastInvocationAware;
import org.springframework.hateoas.server.core.MethodInvocation;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/// A helper for testing HATEOAS-aware controllers.
public abstract class HateoasTest {

    /// A reference to a relation method on an assembler.
    protected interface Relation<C, D> {

        @SuppressWarnings("UnusedReturnValue")
        @Nullable Object call(C controller, D dto) throws Throwable;

    }

    /// A reference to an endpoint method on a controller.
    protected interface Endpoint<C> {

        @SuppressWarnings("UnusedReturnValue")
        @Nullable Object call(C controller) throws Throwable;

    }

    /// Test a relation. A typical invocation looks like this:
    /// ```
    /// assertQuery(dto, fixture::foo, controller -> controller.bar(dto.baz()))
    /// ```
    /// This asserts that the relation `foo` links to the `bar` endpoint, using
    /// the DTO's `baz` method to satisfy the first parameter.
    @SafeVarargs
    @SneakyThrows
    protected static <C, D> void assertQuery(D dto, Relation<C, D> actual, Endpoint<C> expected, C... typeHint) {
        LastInvocationAware holder;
        MethodInvocation invocation;
        @SuppressWarnings("unchecked")
        var type = (Class<C>) typeHint.getClass().getComponentType();
        var proxy = DummyInvocationUtils.methodOn(type);
        expected.call(proxy);
        holder = Objects.requireNonNull(DummyInvocationUtils.getLastInvocationAware(proxy));
        invocation = holder.getLastInvocation();
        var expectedMethod = invocation.getMethod();
        var expectedArguments = invocation.getArguments();
        actual.call(proxy, dto);
        holder = Objects.requireNonNull(DummyInvocationUtils.getLastInvocationAware(proxy));
        invocation = holder.getLastInvocation();
        assertThat(invocation.getMethod()).isEqualTo(expectedMethod);
        assertThat(invocation.getArguments()).containsExactly(expectedArguments);
    }

    /// Assert that a relation is absent for a given DTO.
    @SafeVarargs
    @SneakyThrows
    protected static <C, D> void assertQueryIsNull(D dto, Relation<C, D> actual, C... typeHint) {
        @SuppressWarnings("unchecked")
        var type = (Class<C>) typeHint.getClass().getComponentType();
        var proxy = DummyInvocationUtils.methodOn(type);
        assertThat(actual.call(proxy, dto)).isNull();
    }

}
