package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.*;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import io.github.naomimyselfandi.xanaduwars.util.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Data
@JsonCommentable
@JsonDeserialize
@FieldNameConstants
@EqualsAndHashCode(callSuper = false)
class RulesetImpl implements Ruleset {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull @Valid Version version;

    @JsonDeserialize(contentAs = RuleImpl.class)
    private @NotNull List<@NotNull @Valid Rule> rules = List.of();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid NormalAction> actions = List.of();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid Commander> commanders = List.of();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid Spell> spells = List.of();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid Affinity> affinities = List.of();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid StructureType> structureTypes = List.of();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid TileType> tileTypes = List.of();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid UnitType> unitTypes = List.of();

    private @NotNull Action moveAction;
    private @NotNull Action dropAction;
    private @NotNull Action waitAction;
    private @NotNull Action passAction;
    private @NotNull Action yieldAction;

    @Getter(lazy = true)
    private final Map<String, Object> scriptConstants = initConstants();

    @Override
    public Commander getCommander(CommanderId id) {
        return commanders.get(id.index());
    }

    @Override
    public Spell getSpell(SpellId id) {
        return spells.get(id.index());
    }

    @Override
    public StructureType getStructureType(StructureTypeId id) {
        return structureTypes.get(id.index());
    }

    @Override
    public TileType getTileType(TileTypeId id) {
        return tileTypes.get(id.index());
    }

    @Override
    public UnitType getUnitType(UnitTypeId id) {
        return unitTypes.get(id.index());
    }

    private Map<String, Object> initConstants() {
        var constants = new HashMap<String, Object>();
        for (var constant : constants()) constants.put(constant.toString(), constant);
        return Map.copyOf(constants);
    }

    private List<Object> constants() {
        var declarationsAndTags = Stream.of(
                        actions,
                        commanders,
                        spells,
                        structureTypes,
                        tileTypes,
                        unitTypes,
                        List.of(Iff.values()),
                        List.of(Kind.values())
                )
                .flatMap(List::stream)
                .flatMap(object -> Stream.concat(
                        Stream.of(object),
                        object instanceof Declaration declaration ? declaration.getTags().stream() : Stream.empty()
                ))
                .distinct();
        return Stream.concat(declarationsAndTags, affinities.stream()).toList();
    }

}
