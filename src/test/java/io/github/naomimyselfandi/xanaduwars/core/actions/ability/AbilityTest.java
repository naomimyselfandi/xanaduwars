package io.github.naomimyselfandi.xanaduwars.core.actions.ability;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.naomimyselfandi.xanaduwars.core.filter.Filters;
import io.github.naomimyselfandi.xanaduwars.core.wrapper.*;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class AbilityTest {

    @MethodSource
    @ParameterizedTest
    void json(Ability<?> ability, @Language("json") String json) {
        TestUtils.assertJson(new TypeReference<Ability<?>>() {}, ability, json);
    }

    private static Arguments testCase(Object ability, @Language("json") String json) {
        return arguments(ability, json);
    }

    private static Stream<Arguments> json() {
        return Stream.of(
                testCase(new BuildAbility(new Name("Build"), TagSet.EMPTY),
                        """
                        {"ability": ".BuildAbility", "name": "Build"}
                        """
                ),
                testCase(new RepairAbility(
                                new Name("Repair"),
                                TagSet.EMPTY,
                                Filters.defaultBiFilter(),
                                new Percent(0.25)),
                        """
                        {
                          "ability": ".RepairAbility",
                          "name": "Repair",
                          "baseAmount": 0.25
                        }
                        """)
        );
    }

}
