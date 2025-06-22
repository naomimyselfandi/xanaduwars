package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(SeededRandomExtension.class)
class AccountTest {

    @MethodSource
    @ParameterizedTest
    void hasRole(Role role, boolean expected) {
        var account = new Account();
        var _ = switch (role) {
            case SUPPORT -> account.setSupport(expected);
            case MODERATOR -> account.setModerator(expected);
            case JUDGE -> account.setJudge(expected);
            case ADMIN -> account.setAdmin(expected);
            case DEVELOPER -> account.setDeveloper(expected);
            case BOT -> account.setBot(expected);
        };
        assertThat(account.hasRole(role)).isEqualTo(expected);
    }

    private static Stream<Arguments> hasRole() {
        return Arrays
                .stream(Role.values())
                .flatMap(role -> Stream.of(arguments(role, true), arguments(role, false)));
    }

    @EnumSource
    @ParameterizedTest
    void setRole_True(Role role) {
        var account = new Account();
        assertThat(account.setRole(role, true)).isSameAs(account);
        var expected = transitivelyImpliedRoles(role).collect(Collectors.toSet());
        var actual = Arrays.stream(Role.values()).filter(account::hasRole).collect(Collectors.toSet());
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Role> transitivelyImpliedRoles(Role role) {
        var implications = role.getImpliedRoles().stream().flatMap(AccountTest::transitivelyImpliedRoles);
        return Stream.concat(Stream.of(role), implications);
    }

    @EnumSource
    @ParameterizedTest
    void setRole_False(Role role) {
        var account = new Account();
        grantAllRoles(account);
        assertThat(account.setRole(role, false)).isSameAs(account);
        assertThat(account.hasRole(role)).isFalse();
        assertThat(role.getImpliedRoles()).allMatch(account::hasRole);
    }

    private static void grantAllRoles(Account account) {
        for (var role : Role.values()) {
            account.setRole(role, true);
        }
    }

    @Test
    void prepare(SeededRng random) {
        var account = new Account();
        var username = random.<Username>get();
        assertThat(account.setUsername(username)).isSameAs(account);
        account.prepare();
        assertThat(account.getCanonicalUsername()).isEqualTo(username.toCanonicalForm());
    }

    @Test
    void prepare_ToleratesNullUsername() {
        // We want this to fail during validation so we get a clear error.
        assertThatCode(new Account()::prepare).doesNotThrowAnyException();
    }

}
