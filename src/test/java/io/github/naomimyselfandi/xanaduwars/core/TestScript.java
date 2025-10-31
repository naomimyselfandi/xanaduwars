package io.github.naomimyselfandi.xanaduwars.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.naomimyselfandi.xanaduwars.core.model.Command;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Builder(setterPrefix = "set")
record TestScript(String name, int width, int height, int players, List<Step> steps) {

    @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({@Type(Evaluate.class), @Type(Invalid.class), @Type(PlayerCommand.class), @Type(UnitCommand.class)})
    sealed interface Step {}

    record Evaluate(Script evaluate) implements Step {}

    record Invalid(Step invalid, @Nullable Class<?> exception, @Nullable String message) implements Step {}

    record PlayerCommand(List<Command> cast) implements Step {}

    record UnitCommand(int x, int y, List<Command> use) implements Step {}

    @Override
    public @NotNull String toString() {
        return name;
    }

}
