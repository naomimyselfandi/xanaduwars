package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.Node;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Range;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class FilterImplValidationTest {

    @MethodSource
    @ParameterizedTest
    void isAppropriate_True(BiFilter<Object, Object> filter) {
        assertThat(new FilterImpl<>(filter).isAppropriate()).isTrue();
    }

    @MethodSource
    @ParameterizedTest
    void isAppropriate_False(BiFilter<Object, Object> filter) {
        assertThat(new FilterImpl<>(filter).isAppropriate()).isFalse();
    }

    private static Stream<BiFilter<?, ?>> isAppropriate_True() {
        return Stream.of(
                new BiFilterAdaptor<>(Object.class, new BiFilterYes<>()),
                new BiFilterAll<>(List.of(new BiFilterYes<>())),
                new BiFilterAny<>(List.of(new BiFilterYes<>())),
                new BiFilterNot<>(new BiFilterYes<>()),
                new BiFilterOfName<>(new Name("Foo")),
                new BiFilterOfTag<>(new Tag("Bar")),
                new BiFilterUsingOwner<>(new BiFilterYes<>()),
                new BiFilterUsingTile<>(new BiFilterYes<>()),
                new BiFilterUsingUnit<>(new BiFilterYes<>())
        );
    }

    private static Stream<BiFilter<?, ?>> isAppropriate_False() {
        var main = Stream.<BiFilter<Node, Node>>of(
                new BiFilterOfIff<>(BiFilterOfIff.Iff.OWN),
                new BiFilterOfIff<>(BiFilterOfIff.Iff.ALLY),
                new BiFilterOfIff<>(BiFilterOfIff.Iff.ENEMY),
                new BiFilterOfRange<>(new Range(1, 2))
        ).flatMap(filter -> Stream.of(
                filter,
                new BiFilterAdaptor<>(Node.class, filter),
                new BiFilterAll<>(List.of(filter, new BiFilterYes<>())),
                new BiFilterAll<>(List.of(new BiFilterYes<>(), filter)),
                new BiFilterAny<>(List.of(filter, new BiFilterYes<>())),
                new BiFilterAny<>(List.of(new BiFilterYes<>(), filter)),
                new BiFilterNot<>(filter)
        ));
        return Stream.concat(main, Stream.of(new BiFilterOfSubject<>(new BiFilterYes<>())));
    }

}
