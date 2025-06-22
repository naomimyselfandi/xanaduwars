package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.NodeId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.TileId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.UnitId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

@Converter(autoApply = true)
class NodeIdConverter implements AttributeConverter<NodeId, String> {

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable NodeId attribute) {
        return switch (attribute) {
            case TileId tileId -> "%04d,%04d".formatted(tileId.x(), tileId.y());
            case UnitId unitId -> "Unit,%04d".formatted(unitId.unitId());
            case null -> null;
        };
    }

    @Override
    public @Nullable NodeId convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.startsWith("Unit,")) {
            return new UnitId(Integer.parseInt(dbData.substring(5), 10));
        } else {
            var parts = dbData.split(",", 2);
            return new TileId(Integer.parseInt(parts[0], 10), Integer.parseInt(parts[1], 10));
        }
    }

}
