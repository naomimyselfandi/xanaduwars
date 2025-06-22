package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

/// A bitset used to track spell slot state.
@Data
@Embeddable
public class Bitset implements Serializable {

    private int bits;

    /// Test if a bit is set.
    public boolean test(int bit) {
        return (bits & mask(bit)) != 0;
    }

    /// Set or clear a bit.
    public Bitset set(int bit, boolean value) {
        if (value) {
            bits |= mask(bit);
        } else {
            bits &= ~mask(bit);
        }
        return this;
    }

    private static int mask(int bit) {
        return 1 << bit;
    }

}
