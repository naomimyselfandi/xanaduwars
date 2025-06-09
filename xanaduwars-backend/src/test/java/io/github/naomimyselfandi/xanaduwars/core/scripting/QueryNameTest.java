package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class QueryNameTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            Foo,true
            Bar,true
            Spam,false
            Eggs,false
            """)
    void isDefinedQueryType(String label, boolean expected) {
        assertThat(new QueryName(label).isDefinedQueryType()).isEqualTo(expected);
    }

    @Test
    void of() {
        assertThat(QueryName.of(new FooQuery(new Object(), 0))).isEqualTo(new QueryName("Foo"));
        assertThat(QueryName.of(new BarEvent(new Object()))).isEqualTo(new QueryName("Bar"));
    }

    @Test
    void queryName() {
        assertThat(QueryName.of(new FooQuery(new Object(), 0)).queryName()).isEqualTo("Foo");
        assertThat(QueryName.of(new BarEvent(new Object())).queryName()).isEqualTo("Bar");
    }

    @Test
    void testToString() {
        assertThat(QueryName.of(new FooQuery(new Object(), 0))).hasToString("Foo");
        assertThat(QueryName.of(new BarEvent(new Object()))).hasToString("Bar");
    }

}
