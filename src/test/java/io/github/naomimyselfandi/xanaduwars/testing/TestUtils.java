package io.github.naomimyselfandi.xanaduwars.testing;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/// Utilities for testing. **Only include truly general utilities here.**
/// Utilities related to a specific piece of game logic should live in the
/// same package as that logic.
@UtilityClass
public class TestUtils {

    /// Helper for testing [Comparable#compareTo(Object)]. This takes the given
    /// comparable elements and asserts that:
    /// * Each element returns 0 when compared to itself.
    /// * Each element returns 0 or less when compared to each subsequent
    /// element, and 0 or greater when compared to the previous element.
    /// * Repeatedly shuffling and sorting the list of elements returns the
    /// original list.
    @SafeVarargs
    public <T extends Comparable<T>> void assertSortOrder(T... elements) {
        assertThat(elements).allSatisfy(it -> assertThat(it).isEqualByComparingTo(it));
        for (var i = 1; i < elements.length; i++) {
            var lhs = elements[i - 1];
            var rhs = elements[i];
            assertThat(lhs).isLessThanOrEqualTo(rhs);
            assertThat(rhs).isGreaterThanOrEqualTo(lhs);
        }
        // Now, ensure that repeatedly shuffling and sorting the list is a
        // no-op. We duplicate each element in the original list to ensure
        // stability.
        var expected = Arrays.stream(elements).flatMap(it -> Stream.of(it, it)).toList();
        var actual = new ArrayList<>(expected);
        for (var i = 0; i < 10; i++) {
            do {
                Collections.shuffle(actual);
            } while (actual.equals(expected));
            Collections.sort(actual);
            assertThat(actual).isEqualTo(expected);
        }
    }

    /// Helper for testing JSON serialization. This asserts that the given JSON
    /// string deserializes to the given object, and that the given object
    /// survives a round trip through JSON.
    @SneakyThrows
    public void assertJson(Class<?> type, Object object, @Language("json") String json) {
        var objectMapper = new ObjectMapper()
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature());
        assertThat(objectMapper.readValue(json, type)).isEqualTo(object);
        var roundTrip = objectMapper.writeValueAsBytes(object);
        assertThat(objectMapper.readValue(roundTrip, type)).isEqualTo(object);
    }

    /// Helper for testing JSON serialization. This asserts that the given JSON
    /// string deserializes to the given object, and that the given object
    /// survives a round trip through JSON.
    @SneakyThrows
    public void assertJson(TypeReference<?> type, Object object, @Language("json") String json) {
        var objectMapper = new ObjectMapper().enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature());
        assertThat(objectMapper.readValue(json, type)).isEqualTo(object);
        var roundTrip = objectMapper.writeValueAsBytes(object);
        assertThat(objectMapper.readValue(roundTrip, type)).isEqualTo(object);
    }

    /// Helper for testing JSON serialization. This asserts that the given JSON
    /// string deserializes to the given object, and that the given object
    /// survives a round trip through JSON.
    /// @see #assertJson(Class, Object, String)
    public void assertJson(Object object, @Language("json") String json) {
        assertJson(object.getClass(), object, json);
    }

}
