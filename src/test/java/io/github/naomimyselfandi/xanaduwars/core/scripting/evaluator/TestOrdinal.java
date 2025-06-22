package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.xanaduwars.util.Ordinal;
import lombok.With;

public record TestOrdinal(@With int ordinal) implements Ordinal<TestOrdinal> {

    public static final TestOrdinal ZERO = new TestOrdinal(0);

    public static TestOrdinal of(int ordinal) {
        return ZERO.withOrdinal(ordinal);
    }

}
