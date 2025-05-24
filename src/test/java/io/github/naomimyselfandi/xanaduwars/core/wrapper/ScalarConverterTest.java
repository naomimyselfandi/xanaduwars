package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class ScalarConverterTest {

    private final ScalarConverter fixture = new ScalarConverter();

    @RepeatedTest(10)
    void convertToEntityAttribute(SeededRandom random) {
        var dbData = (double) random.nextInt();
        var attribute = Scalar.withDoubleValue(dbData);
        assertThat(fixture.convertToEntityAttribute(dbData)).isEqualTo(attribute);
    }

    @Test
    void convertToEntityAttribute_Null() {
        assertThat(fixture.convertToEntityAttribute(null)).isNull();
    }

    @RepeatedTest(10)
    void convertToDatabaseColumn(SeededRandom random) {
        var dbData = (double) random.nextInt();
        var attribute = Scalar.withDoubleValue(dbData);
        assertThat(fixture.convertToDatabaseColumn(attribute)).isEqualTo(dbData);
    }

    @Test
    void convertToDatabaseColumn_Null() {
        assertThat(fixture.convertToDatabaseColumn(null)).isNull();
    }

}
