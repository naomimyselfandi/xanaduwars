package io.github.naomimyselfandi.xanaduwars.core.gameplay.entity;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Name;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/// A list of the names of the actions a unit has taken.
@Embeddable
@SuppressWarnings("com.intellij.jpb.NoArgsConstructorInspection")
public record History(@Column(name = "history") String csv) implements Serializable {

    /// An empty action history.
    public static final History NONE = new History("");

    private static final String DELIMITER = ",";

    /// A list of the names of the actions a unit has taken.
    public History(List<Name> names) {
        this(names.stream().map(Name::toString).collect(Collectors.joining(DELIMITER)));
    }

    /// The action names as a list.
    public @Unmodifiable List<Name> names() {
        return Arrays.stream(csv.split(DELIMITER)).map(Name::new).toList();
    }

}
