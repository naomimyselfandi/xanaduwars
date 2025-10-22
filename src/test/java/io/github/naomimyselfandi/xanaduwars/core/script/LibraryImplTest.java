package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class LibraryImplTest {

    private String name;
    private Object value;

    private LibraryImpl fixture;

    @BeforeEach
    void setup(SeededRng random) {
        name = random.nextString();
        value = new Object();
        var map = new HashMap<String, Object>();
        map.put(name, value);
        fixture = new LibraryImpl(map);
    }

    @Test
    void constructor() {
        assertThat(fixture.members()).isUnmodifiable().containsOnly(Map.entry(name, value));
    }

    @Test
    void lookup() {
        assertThat(fixture.lookup(name)).isEqualTo(value);
    }

    @Test
    void lookup_WhenTheNameDoesNotMatchAnyMember_ThenNull(SeededRng random) {
        assertThat(fixture.lookup(random.nextString())).isNull();
    }

}
