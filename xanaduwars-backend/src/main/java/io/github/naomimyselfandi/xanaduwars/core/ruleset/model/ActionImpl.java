package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
@JsonCommentable
class ActionImpl implements Action {

    @JsonProperty
    private @NotNull @Valid Name name;

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

    @Override
    public String toString() {
        return name.name();
    }

}
