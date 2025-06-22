package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class QueryNameTest {

    @Test
    void of() {
        assertThat(QueryName.of(new FooQuery(null, 0))).isEqualTo(new QueryName("Foo"));
        assertThat(QueryName.of(new BarEvent(null))).isEqualTo(new QueryName("Bar"));
    }

    @Test
    void queryName() {
        assertThat(QueryName.of(new FooQuery(null, 0)).queryName()).isEqualTo("Foo");
        assertThat(QueryName.of(new BarEvent(null)).queryName()).isEqualTo("Bar");
    }

    @Test
    void testToString() {
        assertThat(QueryName.of(new FooQuery(null, 0))).hasToString("Foo");
        assertThat(QueryName.of(new BarEvent(null))).hasToString("Bar");
    }

}
