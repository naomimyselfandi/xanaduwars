package io.github.naomimyselfandi.xanaduwars.account.entity;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

class ZoneIdConverterTest {

    private final ZoneIdConverter fixture = new ZoneIdConverter();

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = {"UTC", "America/New_York"})
    void convertToDatabaseColumn(@Nullable String dbData) {
        var entityAttribute = dbData == null ? null : ZoneId.of(dbData);
        assertThat(fixture.convertToDatabaseColumn(entityAttribute)).isEqualTo(dbData);
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = {"UTC", "America/New_York"})
    void convertToEntityAttribute(@Nullable String dbData) {
        var entityAttribute = dbData == null ? null : ZoneId.of(dbData);
        assertThat(fixture.convertToEntityAttribute(dbData)).isEqualTo(entityAttribute);
    }

}
