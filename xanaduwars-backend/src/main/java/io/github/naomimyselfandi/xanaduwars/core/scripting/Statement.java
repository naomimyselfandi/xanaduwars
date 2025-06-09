package io.github.naomimyselfandi.xanaduwars.core.scripting;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.EvaluationContext;

interface Statement {

    void execute(EvaluationContext context) throws Break, Continue, Return;

    // Exception-based flow control is usually a bad idea... but we really do
    // need non-local flow control here, and it ends up being much cleaner than
    // any alternative.

    @EqualsAndHashCode(callSuper = false)
    class Break extends Throwable {

        int depth;

        Break(int depth) {
            super(null, null, false, false);
            this.depth = depth;
        }

    }

    @EqualsAndHashCode(callSuper = false)
    class Continue extends Throwable {

        int depth;

        Continue(int depth) {
            super(null, null, false, false);
            this.depth = depth;
        }

    }

    @EqualsAndHashCode(callSuper = false)
    class Return extends Throwable {

        final @Nullable Object value;

        Return(@Nullable Object value) {
            super(null, null, false, false);
            this.value = value;
        }

    }

}
