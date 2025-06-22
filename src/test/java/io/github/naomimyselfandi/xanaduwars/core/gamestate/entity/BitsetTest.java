package io.github.naomimyselfandi.xanaduwars.core.gamestate.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BitsetTest {

    @Test
    void test() {
        var bitset = new Bitset().set(1, true).set(3, true);
        assertThat(bitset.getBits()).isEqualTo(0b1010);
        assertThat(bitset.test(0)).isFalse();
        assertThat(bitset.test(1)).isTrue();
        assertThat(bitset.test(2)).isFalse();
        assertThat(bitset.test(3)).isTrue();
        assertThat(bitset.test(4)).isFalse();
        bitset.set(1, false).set(2, false);
        assertThat(bitset.getBits()).isEqualTo(0b1000);
        assertThat(bitset.test(0)).isFalse();
        assertThat(bitset.test(1)).isFalse();
        assertThat(bitset.test(2)).isFalse();
        assertThat(bitset.test(3)).isTrue();
        assertThat(bitset.test(4)).isFalse();
    }

}
