package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.BotAccount;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class UserDetailsImplTest {

    private final HumanAccount account = new HumanAccount();
    private final UserDetailsImpl fixture = new UserDetailsImpl(account);

    @RepeatedTest(4)
    void getAuthorities(SeededRng random) {
        for (var role : Role.values()) {
            account.setRole(role, random.nextBoolean());
        }
        var expected = Arrays.stream(Role.values()).filter(account::hasRole).toArray(GrantedAuthority[]::new);
        assertThat(List.<GrantedAuthority>copyOf(fixture.getAuthorities())).containsExactly(expected);
    }

    @Test
    void getUsername(SeededRng random) {
        var username = random.nextUsername();
        account.username(username);
        assertThat(fixture.getUsername()).isEqualTo(username.toString());
    }

    @Test
    void getPassword(SeededRng random) {
        var password = random.nextPassword();
        account.authenticationSecret(password);
        assertThat(fixture.getPassword()).isEqualTo(password.text());
        var botAccount = new BotAccount().authenticationSecret(random.nextAPIKey());
        assertThat(new UserDetailsImpl(botAccount).getPassword()).isNull();
    }

}
