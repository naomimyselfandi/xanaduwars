package io.github.naomimyselfandi.xanaduwars.core.common;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class TagTest {

    @ParameterizedTest
    @MethodSource("constructors")
    void testToString(Function<String, Tag> constructor, SeededRng random) {
        var name = random.<Name>get();
        assertThat(constructor.apply(name.toString())).hasToString(name.toString());
    }

    private static Stream<Function<String, Tag>> constructors() {
        return Stream.of(ActionTag::new, SpellTag::new, StructureTag::new, TileTag::new, UnitTag::new);
    }

}
