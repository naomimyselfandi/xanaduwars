package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.github.naomimyselfandi.xanaduwars.core.common.StructureTypeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import jakarta.validation.Valid;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
class Memory implements Serializable {

    @JsonAnyGetter
    @JsonAnySetter
    private final Map<@NotNull @Valid PlayerId, @NotNull @Valid StructureTypeId> memory = new HashMap<>();

}
