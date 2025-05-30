package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.SpellType;
import lombok.NoArgsConstructor;

/// A DTO used to represent a spell type.
@NoArgsConstructor
public class SpellTypeDto extends TypeDto {

    /// Mapping constructor.
    public SpellTypeDto(SpellType<?> source) {
        super(source);
    }

}
