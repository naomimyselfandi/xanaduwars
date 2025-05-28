package io.github.naomimyselfandi.xanaduwars.gameplay.common;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.intellij.lang.annotations.RegExp;

/// A version number describing a version of the game rules. Version numbers use
/// modified semantic versioning conventions. Since game version numbers have a
/// fundamentally different use case than library version numbers, they do *not*
/// follow the semantic versioning spec exactly:
///
/// - The major component of a version number increases when a version
///   dramatically changes gameplay in some way, such as by overhauling a
///   fundamental game rule or introducing new unit types.
/// - The minor component of a version number increases when a version tweaks
///   balance in a targeted way or adding content which is not expected to
///   significantly impact the metagame. New unit types always require a new
///   major version number, unless they are deployed from a new structure type
///   introduced in the same version, are created only by a spell effect, or are
///   extremely niche (e.g. a unit type intended to provide counterplay against
///   a highly specific build).
/// - The patch component of a version number increases when a version contains
///   only cosmetic changes and bugfixes *that do not break replays*.
/// - Suffixes are used for internal development versions, where they describe
///   why the version was created. Suffixes do not use the same sorting as
///   described in the semantic versioning spec; they are compared strictly
///   lexicographically. Implicitly, a version without a suffix is sorted
///   *before* a version with a suffix and the same numeric components; for
///   example, `1.2.3` sorts before `1.2.3-AirUnitSpeedBuff`.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record Version(
        @Column(name = "version")
        @Pattern(regexp = Version.REGEX) @NotNull @JsonValue String version
) implements Comparable<Version> {

    private static final @RegExp String REGEX = """
            (0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)(?:-|$)[a-zA-Z0-9]*""";

    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile(REGEX);

    @Override
    public int compareTo(Version that) {
        var thisMatcher = PATTERN.matcher(this.version);
        var thatMatcher = PATTERN.matcher(that.version);
        if (thisMatcher.matches() && thatMatcher.matches()) {
            var thisMajor = Integer.parseInt(thisMatcher.group(1));
            var thatMajor = Integer.parseInt(thatMatcher.group(1));
            if (thisMajor != thatMajor) return Integer.compare(thisMajor, thatMajor);
            var thisMinor = Integer.parseInt(thisMatcher.group(2));
            var thatMinor = Integer.parseInt(thatMatcher.group(2));
            if (thisMinor != thatMinor) return Integer.compare(thisMinor, thatMinor);
            var thisPatch = Integer.parseInt(thisMatcher.group(3));
            var thatPatch = Integer.parseInt(thatMatcher.group(3));
            if (thisPatch != thatPatch) return Integer.compare(thisPatch, thatPatch);
        }
        return this.version.compareTo(that.version);
    }

    /// Check if this version number denotes a published game version.
    public boolean isPublished() {
        return !version.contains("-");
    }

    /// This version number's string form.
    @Override
    public String toString() {
        return version;
    }

}
