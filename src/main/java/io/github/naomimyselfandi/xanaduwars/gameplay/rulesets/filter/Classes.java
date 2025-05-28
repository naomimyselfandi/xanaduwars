package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
class Classes {

    final Map<String, Class<? extends Element>> ELEMENT_TYPES_BY_NAME = stream(Element.class)
            .collect(Collectors.toUnmodifiableMap(Class::getSimpleName, c -> c.asSubclass(Element.class)));

    private static Stream<Class<?>> stream(Class<?> c) {
        if (c.isSealed()) {
            return Stream.concat(Stream.of(c), Arrays.stream(c.getPermittedSubclasses()).flatMap(Classes::stream));
        } else {
            return Stream.of(c);
        }
    }

}
