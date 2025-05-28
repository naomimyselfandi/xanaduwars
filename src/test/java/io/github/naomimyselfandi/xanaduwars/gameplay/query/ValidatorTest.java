package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {

    private final Set<Validation> validSet = new HashSet<>();

    private final Validator<Validation> fixture = validSet::contains;

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,false
            false,true
            """)
    void handles(boolean valid, boolean expected) {
        var query = new Validation() {};
        if (valid) validSet.add(query);
        assertThat(fixture.handles(query, true)).isEqualTo(expected);
    }

    @Test
    void handle() {
        assertThat(fixture.handle(new Validation() {}, true)).isFalse();
    }

}
