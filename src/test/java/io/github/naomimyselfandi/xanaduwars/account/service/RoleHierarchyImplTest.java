package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RoleHierarchyImplTest {

    private final RoleHierarchyImpl fixture = new RoleHierarchyImpl();

    @EnumSource
    @ParameterizedTest
    void getReachableGrantedAuthorities_WhenTheUserIsAnAdmin_ThenReturnsAllRoles(Role otherRole) {
        var roles = Set.copyOf(List.of(Role.ADMIN, otherRole));
        assertThat(fixture.getReachableGrantedAuthorities(roles)).isEqualTo(Set.of(Role.values()));
    }

    @RepeatedTest(3)
    void getReachableGrantedAuthorities_WhenTheUserIsNotAnAdmin_ThenReturnsTheInput(SeededRng random) {
        var role1 = random.not(Role.ADMIN);
        var role2 = random.not(Role.ADMIN, role1);
        var roles = Set.of(role1, role2);
        assertThat(fixture.getReachableGrantedAuthorities(roles)).isEqualTo(roles);
    }

}
