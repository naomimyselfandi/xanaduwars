package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

@Converter(autoApply = true)
class NodeIdConverter implements AttributeConverter<NodeId, String> {

    @Override
    public @Nullable NodeId convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.contains(",")) {
            var parts = dbData.split(",", 2);
            return new TileId(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else {
            return new UnitId(Integer.parseInt(dbData));
        }
    }

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable NodeId attribute) {
        return switch (attribute) {
            case TileId tileId -> "%d,%d".formatted(tileId.x(), tileId.y());
            case UnitId unitId -> String.valueOf(unitId.intValue());
            case null -> null;
        };
    }
}
