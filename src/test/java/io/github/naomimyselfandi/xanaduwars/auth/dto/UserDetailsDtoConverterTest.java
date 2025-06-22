package io.github.naomimyselfandi.xanaduwars.auth.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class UserDetailsDtoConverterTest {

    private UserDetailsDtoConverter fixture;

    @BeforeEach
    void setup() {
        fixture = new UserDetailsDtoConverter();
    }

    @Test
    void convert(SeededRng random) {
        var account = new Account()
                .setId(random.get())
                .setUsername(random.get())
                .setPassword(random.get());
        random.shuffle(Role.values()).stream().limit(2).forEach(it -> account.setRole(it, true));
        var roles = Arrays.stream(Role.values()).filter(account::hasRole).toList();
        var expected = new UserDetailsDto()
                .setId(account.getId())
                .setUsername(account.getUsername().username())
                .setPassword(account.getPassword().text())
                .setAuthorities(roles);
        assertThat(fixture.convert(account)).isEqualTo(expected);
    }

}
