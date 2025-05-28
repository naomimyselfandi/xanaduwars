package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.naomimyselfandi.xanaduwars.gameplay.Element;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

record BiFilterOfIff<S extends Element, T extends Element>(@NotNull BiFilterOfIff.Iff iff) implements BiFilter<S, T> {

    enum Iff {OWN, ALLY, ENEMY}

    static final Map<String, BiFilterOfIff<Element, Element>> BY_NAME = Arrays
            .stream(Iff.values())
            .map(BiFilterOfIff::new)
            .collect(Collectors.toUnmodifiableMap(Objects::toString, Function.identity()));

    @Override
    public boolean test(S subject, T target) {
        return switch (iff) {
            case OWN -> target.owner().equals(subject.owner());
            case ALLY -> subject.isAlly(target);
            case ENEMY -> !subject.isAlly(target);
        };
    }

    @Override
    @JsonValue
    public String toString() {
        return switch (iff) {
            case OWN -> "own";
            case ALLY -> "ally";
            case ENEMY -> "enemy";
        };
    }

}
