package io.github.naomimyselfandi.xanaduwars.mapstruct;

import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FlexibleAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    private static final String BASE_PACKAGE_NAME;
    private static final Predicate<String> FLUENT_NAME;
    static {
        BASE_PACKAGE_NAME = FlexibleAccessorNamingStrategy.class
                .getPackageName()
                .replaceFirst("\\.[^.]*$", "");
        FLUENT_NAME = Pattern.compile("^(?:[gs]et|is|has|add)[A-Z].*$").asPredicate().negate();
    }

    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        return super.isGetterMethod(method) || isFluentGetter(method);
    }

    @Override
    public String getPropertyName(ExecutableElement method) {
        if (isFluentGetter(method)) {
            return method.getSimpleName().toString();
        } else {
            return super.getPropertyName(method);
        }
    }

    private static boolean isFluentGetter(ExecutableElement method) {
        return method.getEnclosingElement().asType().toString().startsWith(BASE_PACKAGE_NAME)
                && FLUENT_NAME.test(method.getSimpleName().toString())
                && method.getReturnType().getKind() != TypeKind.VOID
                && method.getParameters().isEmpty();
    }

}
