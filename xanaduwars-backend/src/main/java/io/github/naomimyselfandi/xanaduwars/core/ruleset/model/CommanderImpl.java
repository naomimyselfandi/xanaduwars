package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryName;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
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

    @JsonProperty
    private final @NotNull @Valid CommanderId id;

    @JsonProperty
    private final @NotNull @Valid Name name;

    @JsonProperty
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private @NotNull List<@NotNull @Valid Spell> signatureSpells = List.of();

    @JsonProperty
    private @NotNull Map<@NotNull @Valid SpellTag, @NotNull Affinity> affinities = Map.of();

    @JsonProperty
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
