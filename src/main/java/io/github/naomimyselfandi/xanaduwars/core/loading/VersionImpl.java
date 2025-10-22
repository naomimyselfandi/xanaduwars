package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.message.Rule;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptConstant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

class VersionImpl implements Version, BiConsumer<String, Object>, ScriptConstant {

    private final Map<String, @NotNull @Valid Object> declarations = new LinkedHashMap<>();

    private List<@NotNull @Valid Rule> globalRules = List.of();

    @Setter(AccessLevel.PACKAGE)
    @Getter(onMethod_ = @Override)
    private @NotNull @Valid VersionNumber versionNumber;

    @Setter(AccessLevel.PACKAGE)
    @Getter(onMethod_ = @Override)
    private Ability moveAbility, fireAbility, dropAbility;

    @Override
    public @Unmodifiable List<Rule> getGlobalRules() {
        return globalRules;
    }

    @Override
    public Stream<Object> getDeclarations() {
        return declarations.values().stream();
    }

    @Override
    public @Nullable Object lookup(String name) {
        return declarations.get(name);
    }

    @Override
    public void accept(String name, Object value) {
        var conflict = declarations.put(name, value);
        if (conflict != null) {
            var message = "Got multiple declarations with the same name: %s and %s".formatted(conflict, value);
            throw new IllegalStateException(message);
        }
    }

    void setGlobalRules(List<Rule> globalRules) {
        this.globalRules = List.copyOf(globalRules);
    }

}
