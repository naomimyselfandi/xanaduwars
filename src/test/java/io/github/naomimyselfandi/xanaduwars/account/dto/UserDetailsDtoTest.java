package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class UserDetailsDtoTest {

    private UserDetailsDto fixture;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = random.get();
    }

    @Test
    void getUsername() {
        assertThat(fixture.getUsername()).isEqualTo(fixture.username().username());
    }

    @Test
    void getPassword() {
        assertThat(fixture.getPassword()).isEqualTo(fixture.password().password());
    }

    @Test
    void getAuthorities() {
        assertThat(fixture.getAuthorities()).isEqualTo(fixture.roles().asCollection());
    }

}
