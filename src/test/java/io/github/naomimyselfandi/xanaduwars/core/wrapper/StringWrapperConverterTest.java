package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class StringWrapperConverterTest {

    private record Definition<T extends StringWrapper>(
            StringWrapperConverter<T> converter,
            Function<String, T> factory,
            String... examples
    ) {}

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    private enum Implementation {

        NAME(new Definition<>(new NameConverter(), Name::new, "Foo", "Bar")),

        TAG(new Definition<>(new TagConverter(), Tag::new, "Foo", "Bar")),

        VERSION_NUMBER(new Definition<>(
                new VersionNumberConverter(),
                VersionNumber::new,
                "1.2.3",
                "1.2.3-Foo"
        ));

        private final Definition<?> definition;

    }

    @EnumSource
    @ParameterizedTest
    void convertToEntityAttribute(Implementation implementation) {
        for (var i = 0; i < 10; i++) {
            convertToEntityAttribute(implementation.definition);
        }
    }

    private <T extends StringWrapper> void convertToEntityAttribute(Definition<T> definition) {
        for (var dbData : definition.examples) {
            var attribute = definition.factory.apply(dbData);
            assertThat(definition.converter.convertToEntityAttribute(dbData)).isEqualTo(attribute);
        }
    }

    @EnumSource
    @ParameterizedTest
    void convertToEntityAttribute_Null(Implementation implementation) {
        assertThat(implementation.definition.converter.convertToEntityAttribute(null)).isNull();
    }

    @EnumSource
    @ParameterizedTest
    void convertToDatabaseColumn(Implementation implementation) {
        for (var i = 0; i < 10; i++) {
            convertToDatabaseColumn(implementation.definition);
        }
    }

    private <T extends StringWrapper> void convertToDatabaseColumn(Definition<T> definition) {
        for (var dbData : definition.examples) {
            var attribute = definition.factory.apply(dbData);
            assertThat(definition.converter.convertToDatabaseColumn(attribute)).isEqualTo(dbData);
        }
    }

    @EnumSource
    @ParameterizedTest
    void convertToDatabaseColumn_Null(Implementation implementation) {
        assertThat(implementation.definition.converter.convertToDatabaseColumn(null)).isNull();
    }

}
