package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.actions.*;
import io.github.naomimyselfandi.xanaduwars.ext.JsonComment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonComment
record RulesetDetailsImpl(
        @NotNull @Valid MoveAction moveAction,
        @NotNull @Valid DropAction dropAction,
        @NotNull @Valid AttackAction attackAction,
        @NotNull @Valid WaitAction waitAction,
        @NotNull @Valid DeployAction deployAction,
        @NotNull @Valid PassAction passAction,
        @NotNull @Valid ResignAction resignAction
) implements Ruleset.Details {}
