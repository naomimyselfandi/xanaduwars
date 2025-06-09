package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.naomimyselfandi.xanaduwars.core.scripting.QueryName;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@JsonCommentable
class RuleImpl implements Rule {

    @JsonProperty
    private @NotNull @Valid Name name;

    @JsonProperty
    private @NotEmpty Map<@NotNull @Valid QueryName, @NotNull @Valid Script> handlers = Map.of();

}
