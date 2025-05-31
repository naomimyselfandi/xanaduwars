package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Affinity;
import io.github.naomimyselfandi.xanaduwars.gameplay.Commander;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import io.github.naomimyselfandi.xanaduwars.gameplay.SpellType;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.SubjectRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.TargetRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.CommanderId;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Tag;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.TagSet;
import io.github.naomimyselfandi.xanaduwars.ext.JsonCommentable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

/// The standard commander implementation.
@JsonCommentable
@RequiredArgsConstructor
@Getter(onMethod_ = @JsonGetter)
@Setter(onMethod_ = @JsonSetter, value = AccessLevel.PACKAGE)
public final class CommanderImpl implements Commander {

    private final @NotNull @Valid CommanderId id;

    private final @NotNull Name name;

    private @NotNull TagSet tags = TagSet.EMPTY;

    @JsonDeserialize(contentAs = SubjectRule.class)
    private @NotNull List<@NotNull @Valid Rule<?, ?>> subjectRules = List.of();

    @JsonDeserialize(contentAs = TargetRule.class)
    private @NotNull List<@NotNull @Valid Rule<?, ?>> targetRules = List.of();

    private @NotNull List<@NotNull SpellType<?>> signatureSpells = List.of();

    private @NotNull Map<@NotNull Tag, @NotNull Affinity> affinities = Map.of();

    @Override
    public String toString() {
        return "%s(%s)".formatted(id, name);
    }

}
