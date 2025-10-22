package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(SeededRandomExtension.class)
class AccountTest {

    private final Account fixture = new Account();

    @Test
    void prepare(SeededRng random) {
        var username = random.<Username>get();
        assertThat(fixture.setUsername(username)).isSameAs(fixture);
        fixture.prepare();
        assertThat(fixture.getCanonicalUsername()).isEqualTo(username.toCanonicalForm());
    }

    @Test
    void prepare_ToleratesNullUsername() {
        // We want this to fail during validation so we get a clear error.
        assertThatCode(fixture::prepare).doesNotThrowAnyException();
    }

}
