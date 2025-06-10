package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Rule;
import io.github.naomimyselfandi.xanaduwars.core.scripting.ScriptConstant;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Data
@JsonCommentable
@JsonDeserialize
@FieldNameConstants
class RulesetImpl implements Ruleset {

    @JsonProperty
    @JsonDeserialize(contentAs = RuleImpl.class)
    private @NotNull List<@NotNull @Valid Rule> globalRules = List.of();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid Commander> commanders;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid Spell> spells;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid StructureType> structureTypes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid TileType> tileTypes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @NotNull List<@NotNull @Valid UnitType> unitTypes;

    @JsonProperty
    @JsonDeserialize(contentAs = ActionImpl.class)
    private @NotNull List<@NotNull @Valid Action> commonUnitActions = List.of();

    @JsonProperty
    @JsonDeserialize(contentAs = ActionImpl.class)
    private @NotNull List<@NotNull @Valid Action> commonPlayerActions = List.of();

    @JsonProperty
    private @NotNull @Valid ActionImpl deploymentAction;

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
    public TileType getTileTypes(TileTypeId id) {
        return tileTypes.get(id.index());
    }

    @Override
    public UnitType getUnitType(UnitTypeId id) {
        return unitTypes.get(id.index());
    }

    @Override
    public Stream<ScriptConstant> constants() {
        return Stream.concat(declarations(), declarations().map(Declaration::getTags).flatMap(Set::stream).distinct());
    }

    @Override
    public Stream<Rule> rules() {
        return globalRules.stream();
    }

    private Stream<Declaration> declarations() {
        return Stream.of(commanders, spells, structureTypes, tileTypes, unitTypes).flatMap(List::stream);
    }

}
