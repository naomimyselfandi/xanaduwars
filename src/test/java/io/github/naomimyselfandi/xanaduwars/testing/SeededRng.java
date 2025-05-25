package io.github.naomimyselfandi.xanaduwars.testing;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.*;

import java.util.List;

/// An extension of [SeededRandom] that's aware of our domain types.
public class SeededRng extends SeededRandom {

    @SuppressWarnings("SpellCheckingInspection")
    private static final List<String> ALPHABET = List.of("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));

    public SeededRng(long initialSeed) {
        super(initialSeed);
    }

    public Name nextName() {
        return nextName(pick(ALPHABET));
    }

    public Name nextName(String prefix) {
        return new Name(prefix + nextInt(Integer.MAX_VALUE));
    }

    public Tag nextTag() {
        return nextTag(pick(ALPHABET));
    }

    public Tag nextTag(String prefix) {
        return new Tag(prefix + nextInt(Integer.MAX_VALUE));
    }

    public Percent nextPercent() {
        return Percent.withDoubleValue(nextDouble());
    }

    public Scalar nextScalar() {
        double base = nextInt();
        return Scalar.withDoubleValue(base + nextDouble());
    }

    public Range nextRange() {
        var min = nextInt(Short.MAX_VALUE);
        var max = nextInt(min + 1, Integer.MAX_VALUE);
        return new Range(min, max);
    }

}
