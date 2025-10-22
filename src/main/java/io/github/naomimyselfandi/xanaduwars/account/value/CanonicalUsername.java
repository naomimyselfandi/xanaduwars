package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/// The canonical form of a username. This is used when looking up users by
/// username, both to provide a cleaner experience and to protect against
/// misleading usernames.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record CanonicalUsername(@NotEmpty @JsonValue @Column(name = "canonical_username") String username)
        implements Serializable {

    private static final Pattern PATTERN;
    private static final Function<MatchResult, String> REPLACEMENT;

    /// Construct the canonical form of a username. This is used when looking
    /// up users by username, both to provide a cleaner experience and to
    /// protect against misleading usernames.
    public CanonicalUsername(String username) {
        this.username = Normalizer
                .normalize(username, Normalizer.Form.NFKD)
                .toLowerCase(Locale.ROOT)
                .transform(PATTERN::matcher)
                .replaceAll(REPLACEMENT);
    }

    @Override
    public String toString() {
        return username;
    }

    static {
        var lookalikes = Map.ofEntries(
                Map.entry("3", "e"),
                Map.entry("1", "l"),
                Map.entry("0", "o"),
                Map.entry("ß", "ss"),
                Map.entry("æ", "ae"),
                Map.entry("œ", "oe"),
                Map.entry("ø", "o"),
                Map.entry("đ", "d"),
                Map.entry("ħ", "h"),
                Map.entry("þ", "th"),
                Map.entry("ð", "d"),
                Map.entry("ł", "l"),
                Map.entry("ŋ", "ng"),
                Map.entry("μ", "u"),
                Map.entry("ı", "i"),
                Map.entry("ŧ", "t"),
                Map.entry("ƒ", "f"),
                Map.entry(" ", "-"),
                Map.entry("_", "-")
        );
        var escapedLookalikes = lookalikes.keySet().stream().map(Pattern::quote).collect(Collectors.joining());
        PATTERN = Pattern.compile("[\\p{M}¿¡" + escapedLookalikes + "]");
        REPLACEMENT = matchResult -> lookalikes.getOrDefault(matchResult.group(), "");
    }

}
