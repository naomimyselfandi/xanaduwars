package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.util.regex.Pattern;

/// The number of a game version.
/// @param major The major component of the version number. This increases with
/// major gameplay changes that may significantly change the metagame.
/// @param minor The minor component of the version number. This increases with
/// every game balance adjustment or changes that only enable new map features.
/// @param patch The patch component of the version number. This increases with
/// cosmetic changes or bugfixes.
/// @param suffix A development suffix. If this is empty, it denotes a publicly
/// available version; if not, it denotes an internal development version.
public record VersionNumber(
        @PositiveOrZero int major,
        @PositiveOrZero int minor,
        @PositiveOrZero int patch,
        @NotNull String suffix
) implements Comparable<VersionNumber>, Serializable {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(?:-|$)(.*)");

    @JsonCreator
    public static VersionNumber of(String string) {
        var matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            var major = Integer.parseInt(matcher.group(1), 10);
            var minor = Integer.parseInt(matcher.group(2), 10);
            var patch = Integer.parseInt(matcher.group(3), 10);
            var suffix = matcher.group(4);
            return new VersionNumber(major, minor, patch, suffix);
        } else {
            throw new IllegalArgumentException("Invalid version number '%s'.".formatted(string));
        }
    }

    @Override
    public int compareTo(VersionNumber that) {
        var major = Integer.compare(this.major, that.major);
        if (major != 0) return major;
        var minor = Integer.compare(this.minor, that.minor);
        if (minor != 0) return minor;
        var patch = Integer.compare(this.patch, that.patch);
        if (patch != 0) return patch;
        return -this.suffix.compareTo(that.suffix);
    }

    @Override
    @JsonValue
    public String toString() {
        if (suffix.isEmpty()) {
            return "%s.%s.%s".formatted(major, minor, patch);
        } else {
            return "%s.%s.%s-%s".formatted(major, minor, patch, suffix);
        }
    }

}
