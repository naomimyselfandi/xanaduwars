package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import io.github.naomimyselfandi.xanaduwars.ext.JsonComment;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonComment
final class RulesetImpl implements Ruleset {

    private @NotNull Version version;

    private @NotNull List<@NotNull @Valid Rule<?, ?>> globalRules = List.of();

    private @NotNull List<@NotNull @Valid Commander> commanders = List.of();

    private @NotNull List<@NotNull @Valid SpellType<?>> spellTypes = List.of();

    private @NotNull List<@NotNull @Valid TileType> tileTypes = List.of();

    private @NotNull List<@NotNull @Valid UnitType> unitTypes = List.of();

    private @NotNull @Valid RulesetDetailsImpl details;

    @Override
    public String toString() {
        return "Ruleset(%s)".formatted(version);
    }

}
