package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

/// A DTO used to represent a commander.
@Data
public class CommanderDto {
    private CommanderId id;
    private Name name;
    private TagSet tags;
    private List<Name> signatureSpells;
    private Map<Tag, Affinity> affinities;
}
