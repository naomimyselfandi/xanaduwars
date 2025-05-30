package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.ext.FieldIteration;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/// A DTO used to present rulesets.
@Data
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public class RulesetDto {

    private Version version;
    private List<CommanderDto> commanders;
    private List<SpellTypeDto> spellTypes;
    private List<TileTypeDto> tileTypes;
    private List<UnitTypeDto> unitTypes;

    /// Mapping constructor.
    public RulesetDto(Ruleset source) {
        FieldIteration.forEachField(Fields.values(), field -> switch (field) {
            case version -> this.version = source.version();
            case commanders -> this.commanders = source.commanders().stream().map(CommanderDto::new).toList();
            case spellTypes -> this.spellTypes = source.spellTypes().stream().map(SpellTypeDto::new).toList();
            case tileTypes -> this.tileTypes = source.tileTypes().stream().map(TileTypeDto::new).toList();
            case unitTypes -> this.unitTypes = source.unitTypes().stream().map(UnitTypeDto::new).toList();
        });
    }

}
