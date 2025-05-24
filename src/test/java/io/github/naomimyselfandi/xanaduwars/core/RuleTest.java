package io.github.naomimyselfandi.xanaduwars.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleTest {

    private interface TestQuery extends Query<Object> {}

    private final Rule<TestQuery, Object> fixture = new Rule<>() {

        @Override
        public String name() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean handles(TestQuery query, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object handle(TestQuery query, Object value) {
            throw new UnsupportedOperationException();
        }

    };

    @Test
    void queryType() {
        assertThat(fixture.queryType()).isEqualTo(TestQuery.class);
    }

}
