package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class IntWrapperConverterTest {

    private record Definition<T extends IntWrapper>(IntWrapperConverter<T> converter, IntFunction<T> factory) {}

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    private enum Implementation {

        NODE_ID_CONVERTER(new Definition<>(new NodeIdConverter(), NodeId::withIntValue)),

        PLAYER_ID_CONVERTER(new Definition<>(new PlayerIdConverter(), PlayerId::new));

        private final Definition<?> definition;

    }

    @EnumSource
    @ParameterizedTest
    void convertToEntityAttribute(Implementation implementation, SeededRandom random) {
        for (var i = 0; i < 10; i++) {
            convertToEntityAttribute(implementation.definition, random);
        }
    }

    private <T extends IntWrapper> void convertToEntityAttribute(Definition<T> definition, SeededRandom random) {
        var dbData = random.nextInt();
        var attribute = definition.factory.apply(dbData);
        assertThat(definition.converter.convertToEntityAttribute(dbData)).isEqualTo(attribute);
    }

    @EnumSource
    @ParameterizedTest
    void convertToEntityAttribute_Null(Implementation implementation) {
        assertThat(implementation.definition.converter.convertToEntityAttribute(null)).isNull();
    }

    @EnumSource
    @ParameterizedTest
    void convertToDatabaseColumn(Implementation implementation, SeededRandom random) {
        for (var i = 0; i < 10; i++) {
            convertToDatabaseColumn(implementation.definition, random);
        }
    }

    private <T extends IntWrapper> void convertToDatabaseColumn(Definition<T> definition, SeededRandom random) {
        var dbData = random.nextInt();
        var attribute = definition.factory.apply(dbData);
        assertThat(definition.converter.convertToDatabaseColumn(attribute)).isEqualTo(dbData);
    }

    @EnumSource
    @ParameterizedTest
    void convertToDatabaseColumn_Null(Implementation implementation) {
        assertThat(implementation.definition.converter.convertToDatabaseColumn(null)).isNull();
    }

}
