package io.github.naomimyselfandi.xanaduwars.account.value;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;

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

    @EnumSource
    @ParameterizedTest
    void impliedRoles(Role role) {
        assertThat(role.impliedRoles()).isEqualTo(switch (role) {
            case SUPPORT, JUDGE, MODERATOR -> Set.of();
            case DEVELOPER -> Set.of(Role.SUPPORT);
            case ADMIN -> Set.of(Role.DEVELOPER, Role.MODERATOR, Role.JUDGE);
        });
    }

    @EnumSource
    @ParameterizedTest
    void impliedRoles_HaveNoCycles(Role role) {
        class Visitor extends HashSet<Role> implements Consumer<Role> {
            @Override
            public void accept(Role role) {
                assertThat(add(role)).isTrue();
                role.impliedRoles().forEach(this);
            }
        }
        new Visitor().accept(role);
    }

}
