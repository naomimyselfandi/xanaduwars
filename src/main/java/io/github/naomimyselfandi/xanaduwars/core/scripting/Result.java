package io.github.naomimyselfandi.xanaduwars.core.scripting;

/// A successful or unsuccessful result. Query evaluators treat results somewhat
/// specially: an unsuccessful result immediately short circuits the evaluation,
/// even if there are further rules which would otherwise be considered.
public sealed interface Result {

    /// A successful result.
    record Okay() implements Result {
        private static final Okay OKAY = new Okay();
    }

    /// Get a successful result.
    static Okay okay() {
        return Okay.OKAY;
    }

    /// An unsuccessful result.
    record Fail(String message) implements Result {}

    /// Get an unsuccessful result.
    static Fail fail(String message) {
        return new Fail(message);
    }

}
