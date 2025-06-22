package io.github.naomimyselfandi.xanaduwars.e2e;

import org.junit.jupiter.api.extension.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class E2ETestExtension implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        return Arrays
                .stream(context.getRequiredTestMethod().getAnnotationsByType(E2ETest.class))
                .flatMap(login -> IntStream.range(0, login.repetitions()).mapToObj(_ -> login))
                .map(LoginResolver::new);
    }

    private record LoginResolver(E2ETest test) implements TestTemplateInvocationContext, ParameterResolver {

        @Override
        public String getDisplayName(int index) {
            var result = new StringBuilder("iteration ").append(index).append(", ");
            if (test.value()) {
                result.append("roles=").append(Arrays.toString(test.roles()));
            } else {
                result.append("unauthenticated");
            }
            return result.toString();
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return List.of(this);
        }

        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            var type = parameterContext.getParameter().getType();
            return type == E2ETest.class || type.isEnum() || type == String.class;
        }

        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            var type = parameterContext.getParameter().getType();
            if (type == E2ETest.class) {
                return test;
            } else if (type.isEnum()) {
                return Enum.valueOf(type.asSubclass(Enum.class), test.payload());
            } else {
                return test.payload();
            }
        }

    }

}
