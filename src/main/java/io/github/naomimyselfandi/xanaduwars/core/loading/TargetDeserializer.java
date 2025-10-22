package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import io.github.naomimyselfandi.xanaduwars.core.model.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TargetDeserializer extends StdDeserializer<Target<?>> {

    TargetDeserializer() {
        super(Target.class);
    }

    @Override
    public Target<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        var string = parser.getValueAsString();
        var attempt = new Attempt(string);
        var result = attempt.build();
        if (attempt.remaining.isEmpty()) {
            return result;
        } else {
            var message = "Unexpected input in target specification '%s': '%s'.".formatted(string, attempt.remaining);
            throw ValueInstantiationException.from(parser, message, context.getContextualType());
        }
    }

    private static class Attempt {

        private static final Pattern TILE = Pattern.compile("^tile");
        private static final Pattern UNIT = Pattern.compile("^unit");

        private static final Pattern ALLY = Pattern.compile("^\\.ally");
        private static final Pattern ENEMY = Pattern.compile("^\\.enemy");
        private static final Pattern VISIBLE = Pattern.compile("^\\.visible");

        private static final Pattern DOT_TILE = Pattern.compile("^\\.tile");
        private static final Pattern RANGE = Pattern.compile("^\\[([0-9]),([0-9])]");
        private static final Pattern TAGS = Pattern.compile("^\\[([a-zA-Z]+(?:\\|[a-zA-Z]+)*)]");

        private String remaining;

        private Matcher matcher;

        Attempt(String remaining) {
            this.remaining = remaining.replaceAll("\\s+", "");
        }

        Target<?> build() {
            if (take(TILE)) {
                return buildTileTarget();
            } else if (take(UNIT)) {
                return buildUnitTarget();
            } else {
                return TargetOfNothing.NOTHING;
            }
        }

        private Target<Tile> buildTileTarget() {
            if (take(RANGE)) {
                var min = Integer.parseInt(matcher.group(1), 10);
                var max = Integer.parseInt(matcher.group(2), 10);
                return new TargetOfRange<>(buildTileTarget(), min, max);
            } else if (take(TAGS)) {
                var tags = Arrays.stream(matcher.group(1).split("\\|")).map(TileTag::new).toList();
                return new TargetOfTileTag(buildTileTarget(), tags);
            } else if (take(VISIBLE)) {
                return new TargetOfVision<>(buildTileTarget());
            } else {
                return TargetOfTile.TILE;
            }
        }

        private Target<Unit> buildUnitTarget() {
            if (take(DOT_TILE)) {
                return new TargetOfUnit(buildTileTarget());
            } else if (take(ALLY)) {
                return new TargetOfAllyUnit(buildUnitTarget());
            } else if (take(ENEMY)) {
                return new TargetOfEnemyUnit(buildUnitTarget());
            } else if (take(RANGE)) {
                var min = Integer.parseInt(matcher.group(1), 10);
                var max = Integer.parseInt(matcher.group(2), 10);
                return new TargetOfRange<>(buildUnitTarget(), min, max);
            } else if (take(TAGS)) {
                var tags = Arrays.stream(matcher.group(1).split("\\|")).map(UnitTag::new).toList();
                return new TargetOfUnitTag(buildUnitTarget(), tags);
            } else {
                return new TargetOfUnit(TargetOfTile.TILE);
            }
        }

        private boolean take(Pattern pattern) {
            matcher = pattern.matcher(remaining);
            if (matcher.find()) {
                remaining = remaining.substring(matcher.group().length()).trim();
                return true;
            } else {
                return false;
            }
        }

    }

}
