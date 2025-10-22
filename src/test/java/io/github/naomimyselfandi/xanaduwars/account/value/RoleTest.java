package io.github.naomimyselfandi.xanaduwars.account.value;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @EnumSource
    @ParameterizedTest
    void getAuthority(Role role) {
        assertThat(role.getAuthority()).isEqualTo(switch (role) {
            case SUPPORT -> "ROLE_SUPPORT";
            case DEVELOPER -> "ROLE_DEVELOPER";
            case MODERATOR -> "ROLE_MODERATOR";
            case JUDGE -> "ROLE_JUDGE";
            case ADMIN -> "ROLE_ADMIN";
        });
    }

}
