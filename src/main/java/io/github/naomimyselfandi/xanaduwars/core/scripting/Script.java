package io.github.naomimyselfandi.xanaduwars.core.scripting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.naomimyselfandi.xanaduwars.core.scripting.json.ScriptDeserializer;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

/// A script used to define game rules. Defining game rules in scripts allows
/// multiple versions of the game to coexist, which is essential for playtesting
/// and to protect historical replays. Additionally, rule logic is generally
/// straightforward but changes relatively frequently, making it a good match
/// for dynamically typed scripts.
@JsonDeserialize(using = ScriptDeserializer.class)
public interface Script {

    /// A script that does nothing.
    Script NULL = ScriptDeserializer.NULL;

    /// Run this script, specifying the context and return type.
    @Nullable Object run(EvaluationContext context, Class<?> type);

}
