package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;
import lombok.Data;

import java.util.List;

/// A DTO used to represent a ruleset.
@Data
public class RulesetDto {
    private Version version;
    private List<CommanderDto> commanders;
    private List<SpellTypeDto> spellTypes;
    private List<TileTypeDto> tileTypes;
    private List<UnitTypeDto> unitTypes;
}
