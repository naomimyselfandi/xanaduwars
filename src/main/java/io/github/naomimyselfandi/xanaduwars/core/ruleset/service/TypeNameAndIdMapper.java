package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import lombok.AccessLevel;
import lombok.Getter;
import org.mapstruct.Mapper;
import org.mapstruct.extensions.spring.DelegatingConverter;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
class TypeNameAndIdMapper {

    @Autowired
    private RulesetService rulesetService;

    @Autowired
    private VersionService versionService;

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final Ruleset ruleset = rulesetService.load(versionService.published().getFirst());

    @DelegatingConverter
    Name toCommanderName(CommanderId id) {
        return getRuleset().getCommander(id).getName();
    }

    @DelegatingConverter
    Name toSpellName(SpellId id) {
        return getRuleset().getSpell(id).getName();
    }

    @DelegatingConverter
    Name toStructureTypeName(StructureTypeId id) {
        return getRuleset().getStructureType(id).getName();
    }

    @DelegatingConverter
    Name toTileTypeName(TileTypeId id) {
        return getRuleset().getTileType(id).getName();
    }

    @DelegatingConverter
    Name toUnitTypeName(UnitTypeId id) {
        return getRuleset().getUnitType(id).getName();
    }

    @DelegatingConverter
    CommanderId toCommanderId(Name name) {
        var commanders = getRuleset().getCommanders();
        return commanders
                .stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElse(commanders.getFirst())
                .getId();
    }

    @DelegatingConverter
    SpellId toSpellId(Name name) {
        var spells = getRuleset().getSpells();
        return spells
                .stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElse(spells.getFirst())
                .getId();
    }

    @DelegatingConverter
    StructureTypeId toStructureTypeId(Name name) {
        var structureTypes = getRuleset().getStructureTypes();
        return structureTypes
                .stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElse(structureTypes.getFirst())
                .getId();
    }

    @DelegatingConverter
    TileTypeId toTileTypeId(Name name) {
        var tileTypes = getRuleset().getTileTypes();
        return tileTypes
                .stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElse(tileTypes.getFirst())
                .getId();
    }

    @DelegatingConverter
    UnitTypeId toUnitTypeId(Name name) {
        var unitTypes = getRuleset().getUnitTypes();
        return unitTypes
                .stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElse(unitTypes.getFirst())
                .getId();
    }

}
