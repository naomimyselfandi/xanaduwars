package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

@Converter(autoApply = true)
class ActorIdConverter implements AttributeConverter<ActorId, String> {

    private static final int DECIMAL = 10;

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable ActorId attribute) {
        return switch (attribute) {
            case PlayerId playerId -> "P%06d".formatted(playerId.playerId());
            case StructureId structureId -> {
                var tileId = structureId.tileId();
                yield "%03d,%03d".formatted(tileId.x(), tileId.y());
            }
            case UnitId unitId -> "%07d".formatted(unitId.unitId());
            case null -> null;
        };
    }

    @Override
    public @Nullable ActorId convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.startsWith("P")) {
            return new PlayerId(Integer.parseInt(dbData.substring(1), DECIMAL));
        } else if (dbData.contains(",")) {
            var parts = dbData.split(",", 2);
            var tileId = new TileId(Integer.parseInt(parts[0], 10), Integer.parseInt(parts[1], DECIMAL));
            return new StructureId(tileId);
        } else {
            return new UnitId(Integer.parseInt(dbData, DECIMAL));
        }
    }

}
