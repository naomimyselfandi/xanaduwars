package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

@Converter(autoApply = true)
class ActorIdConverter implements AttributeConverter<ActorId, String> {

    @Override
    public @Nullable ActorId convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.startsWith("P")) {
            return new PlayerId(Integer.parseInt(dbData.substring(1)));
        } else if (dbData.contains(",")) {
            var parts = dbData.split(",", 2);
            return new TileId(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else {
            return new UnitId(Integer.parseInt(dbData));
        }
    }

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable ActorId attribute) {
        return switch (attribute) {
            case PlayerId playerId -> "P" + playerId.intValue();
            case TileId tileId -> "%d,%d".formatted(tileId.x(), tileId.y());
            case UnitId unitId -> String.valueOf(unitId.intValue());
            case null -> null;
        };
    }
}
