package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.account.value.RoleSet;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RoleServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RoleServiceImpl fixture;

    @EnumSource
    @ParameterizedTest
    void hasRole_WhenTheUserIsAnAdmin_ThenTheyHaveEveyRole(Role otherRole, SeededRng random) {
        var user = random
                .<UserDetailsDto>get()
                .toBuilder()
                .roles(RoleSet.of(Set.of(Role.ADMIN, random.not(Role.ADMIN, otherRole))))
                .build();
        assertThat(fixture.hasRole(user, otherRole)).isTrue();
    }

    @RepeatedTest(3)
    void hasRole_WhenTheUserIsNotAnAdmin_ThenHaveTheirExplicitlyGrantedRoles(SeededRng random) {
        var role1 = random.not(Role.ADMIN);
        var role2 = random.not(Role.ADMIN, role1);
        var user = random
                .<UserDetailsDto>get()
                .toBuilder()
                .roles(RoleSet.of(Set.of(role1, role2)))
                .build();
        assertThat(fixture.hasRole(user, role1)).isTrue();
        assertThat(fixture.hasRole(user, role2)).isTrue();
        assertThat(fixture.hasRole(user, Role.ADMIN)).isFalse();
        assertThat(fixture.hasRole(user, random.not(role1, role2))).isFalse();
    }

    @Test
    void setRoles(SeededRng random) {
        var id = random.<Id<Account>>get();
        var role1 = random.<Role>get();
        var role2 = random.not(role1);
        var roles = Set.of(role1, role2);
        fixture.setRoles(id, roles);
        verify(accountRepository).updateRolesById(id, RoleSet.of(roles));
    }

}
