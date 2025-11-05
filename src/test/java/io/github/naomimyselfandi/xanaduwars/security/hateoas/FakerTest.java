package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.security.service.AuditService;
import io.github.naomimyselfandi.xanaduwars.util.Cleanup;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class FakerTest {

    private sealed interface SomeSealedType {}

    private enum SomeEnum implements SomeSealedType {FOO, BAR}

    private interface SomeInterface {
        SomeEnum foobar();
    }

    public abstract static class SomeClass {
        abstract SomeEnum foobar();
    }

    private static final class SomeFinalClass {}

    private record SomeRecord() {}

    @Mock
    private Authentication authentication;

    @Mock
    private AuditService auditService;

    @Mock
    private Cleanup cleanup;

    @Mock
    private AuthorizationManager<MethodInvocation> authorizationManager;

    @MethodSource
    @ParameterizedTest
    void $(@Nullable Object expected, Object[] type) {
        assertThat(Faker.$(type)).isEqualTo(expected).isEqualTo(Faker.$(type));
    }

    @Test
    void $_Interface() {
        assertThat(Faker.<SomeInterface>$())
                .isInstanceOf(SomeInterface.class)
                .isEqualTo(Faker.<SomeInterface>$())
                .returns(SomeEnum.FOO, SomeInterface::foobar)
                .hasToString("<<placeholder>>");
    }

    @Test
    void $_Class() {
        assertThat(Faker.<SomeClass>$())
                .isInstanceOf(SomeClass.class)
                .isEqualTo(Faker.<SomeClass>$())
                .returns(SomeEnum.FOO, SomeClass::foobar)
                .hasToString("<<placeholder>>");
    }

    @SafeVarargs
    private static <T> Arguments arguments(T expected, T... typeHint) {
        return Arguments.of(expected, typeHint);
    }

    private static Stream<Arguments> $() {
        return Stream.of(
                arguments(SomeEnum.FOO),
                arguments((SomeSealedType) SomeEnum.FOO),
                arguments((SomeRecord) null),
                arguments((SomeFinalClass) null),
                arguments(List.of()),
                arguments(List.of()),
                arguments(List.of()),
                arguments(Set.of()),
                arguments(Map.of()),
                arguments(Optional.empty()),
                arguments(OptionalInt.empty()),
                arguments(OptionalDouble.empty()),
                arguments(OptionalLong.empty()),
                arguments(""),
                arguments(false),
                arguments(false),
                arguments(0),
                arguments(0),
                arguments(0.0),
                arguments(0.0),
                arguments(0L),
                arguments(0L)
        );
    }

}
