package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import lombok.Data;

/// A DTO used to represent a spell type.
@Data
public class SpellTypeDto {
    private SpellTypeId id;
    private Name name;
    private TagSet tags;
}
