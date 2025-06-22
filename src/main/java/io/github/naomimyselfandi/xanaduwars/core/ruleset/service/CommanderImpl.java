package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.core.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.common.Name;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellTag;
import io.github.naomimyselfandi.xanaduwars.core.common.Tag;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Affinity;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryName;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.util.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@JsonCommentable
@RequiredArgsConstructor
class CommanderImpl implements Commander {

    private final @NotNull @Valid CommanderId id;

    private final @NotNull @Valid Name name;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private @NotNull List<@NotNull @Valid Spell> signatureSpells = List.of();

    private int chosenSpells = 2;

    private @NotNull Map<@NotNull @Valid SpellTag, @NotNull Affinity> affinities = Map.of();

    private @NotNull Map<@NotNull @Valid QueryName, @NotNull @Valid Script> handlers = Map.of();

    @Override
    public @Unmodifiable Set<Tag> getTags() {
        return Set.of();
    }

    @Override
    public String toString() {
        return name.name();
    }

}
