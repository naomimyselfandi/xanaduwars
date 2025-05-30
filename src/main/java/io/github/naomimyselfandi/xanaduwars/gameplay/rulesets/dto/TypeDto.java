package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.xanaduwars.ext.FieldIteration;
import io.github.naomimyselfandi.xanaduwars.gameplay.Type;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TypeId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/// A DTO used to present a type.
@Data
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public abstract class TypeDto {

    private TypeId id;
    private Name name;
    private TagSet tags;

    TypeDto(Type source) {
        FieldIteration.forEachField(Fields.values(), field -> switch (field) {
            case id -> this.id = source.id();
            case name -> this.name = source.name();
            case tags -> this.tags = source.tags();
        });
    }

}
