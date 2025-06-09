package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.naomimyselfandi.xanaduwars.core.scripting.*;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@JsonCommentable
@RequiredArgsConstructor
class SpellImpl implements Spell {

    @JsonProperty
    private final @NotNull @Valid SpellId id;

    @JsonProperty
    private final @NotNull @Valid Name name;

    @JsonProperty
    private final @NotNull Set<@NotNull @Valid SpellTag> tags;

    @JsonProperty
    private @NotNull List<@NotNull TargetSpec> targets = List.of();

    @JsonProperty
    private @Nullable IFF alignment;

    @JsonProperty
    private @NotNull @Valid Script precondition = Script.NULL;

    @JsonProperty
    private @NotNull @Valid Script validation = Script.NULL;

    @JsonProperty
    private @NotNull @Valid Script effect = Script.NULL;

    @JsonProperty
    private @NotNull @Valid Script supplyCost = Script.NULL;

    @JsonProperty
    private @NotNull @Valid Script aetherCost = Script.NULL;

    @JsonProperty
    private @Positive int focusCost;

    @JsonProperty
    private @NotNull Map<@NotNull @Valid QueryName, @NotNull @Valid Script> handlers = Map.of();

    @Override
    public String toString() {
        return name.name();
    }

}
