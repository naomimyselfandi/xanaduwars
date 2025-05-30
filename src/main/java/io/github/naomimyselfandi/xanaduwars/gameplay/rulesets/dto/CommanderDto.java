package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.ext.FieldIteration;
import io.github.naomimyselfandi.xanaduwars.gameplay.Commander;
import io.github.naomimyselfandi.xanaduwars.gameplay.SpellType;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Affinity;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.SpellTypeId;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.Map;

/// A DTO used to represent a commander.
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public class CommanderDto extends TypeDto {

    private List<SpellTypeId> signatureSpells;
    private Map<Tag, Affinity> affinities;

    /// Mapping constructor.
    public CommanderDto(Commander source) {
        super(source);
        FieldIteration.forEachField(Fields.values(), field -> switch (field) {
            case signatureSpells -> this.signatureSpells = source
                    .signatureSpells()
                    .stream()
                    .map(SpellType::id)
                    .toList();
            case affinities -> this.affinities = Map.copyOf(source.affinities());
        });
    }

}
