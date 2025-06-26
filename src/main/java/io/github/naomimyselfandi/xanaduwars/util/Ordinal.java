package io.github.naomimyselfandi.xanaduwars.util;

import org.jetbrains.annotations.Contract;

/// An integer-like object. This interface is a mixin, allowing integer values
/// to be given domain-specific meaning while retaining access to the standard
/// arithmetic operations.
public interface Ordinal<Self extends Ordinal<Self>> extends Comparable<Self> {

    /// Addition.
    default Self plus(int value) {
        return withOrdinal(ordinal() + value);
    }

    /// Subtraction.
    @Contract(pure = true)
    default Self minus(int value) {
        return withOrdinal(ordinal() - value);
    }

    /// Multiplication.
    @Contract(pure = true)
    default Self times(int value) {
        return withOrdinal(ordinal() * value);
    }

    /// Integer division.
    @Contract(pure = true)
    default Self dividedBy(int value) {
        return withOrdinal(ordinal() / value);
    }

    /// Modulus division.
    @Contract(pure = true)
    default Self modulo(int value) {
        return withOrdinal(ordinal() % value);
    }

    /// View this object as a native Java integer.
    int ordinal();

    /// Wrap a native Java integer in an instance of this type.
    Self withOrdinal(int ordinal);

    @Override
    default int compareTo(Self that) {
        return Integer.compare(this.ordinal(), that.ordinal());
    }

}
