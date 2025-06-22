package io.github.naomimyselfandi.xanaduwars.core.ruleset;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Script;
import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;
import org.springframework.expression.EvaluationContext;

/// A script that wraps a constant integer. This type allows specialized action
/// types to guarantee that their cost is fixed, which in turn allows game rules
/// to use that cost in calculations.
@JsonDeserialize
public record FixedCost(@With @JsonValue @PositiveOrZero int ordinal) implements Script, Ordinal<FixedCost> {

    /// A cost of zero.
    public static final FixedCost ZERO = new FixedCost(0);

    @Override
    public Object run(EvaluationContext context, Class<?> type) {
        return ordinal;
    }

    @Override
    public String toString() {
        return String.valueOf(ordinal);
    }

}
