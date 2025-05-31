package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(SeededRandomExtension.class)
class AccountTest {

    @MethodSource
    @ParameterizedTest
    void hasRole(Role role, boolean bot, boolean expected) {
        var account = bot ? new BotAccount() : new HumanAccount();
        var _ = switch (role) {
            case SUPPORT -> account.support(expected);
            case MODERATOR -> account.moderator(expected);
            case JUDGE -> account.judge(expected);
            case ADMIN -> account.admin(expected);
            case DEVELOPER -> account.developer(expected);
        };
        assertThat(account.hasRole(role)).isEqualTo(expected);
    }

    private static Stream<Arguments> hasRole() {
        return Arrays.stream(Role.values()).flatMap(role -> Stream.of(
                arguments(role, true, true),
                arguments(role, false, true),
                arguments(role, true, false),
                arguments(role, false, false)
        ));
    }

    @EnumSource
    @ParameterizedTest
    void setRole_True(Role role) {
        var account = new HumanAccount();
        assertThat(account.setRole(role, true)).isSameAs(account);
        var expected = transitivelyImpliedRoles(role).collect(Collectors.toSet());
        var actual = Arrays.stream(Role.values()).filter(account::hasRole).collect(Collectors.toSet());
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Role> transitivelyImpliedRoles(Role role) {
        var implications = role.impliedRoles().stream().flatMap(AccountTest::transitivelyImpliedRoles);
        return Stream.concat(Stream.of(role), implications);
    }

    @EnumSource
    @ParameterizedTest
    void setRole_False(Role role) {
        var account = new HumanAccount();
        grantAllRoles(account);
        assertThat(account.setRole(role, false)).isSameAs(account);
        assertThat(account.hasRole(role)).isFalse();
        assertThat(role.impliedRoles()).allMatch(account::hasRole);
    }

    private static void grantAllRoles(Account account) {
        for (var role : Role.values()) {
            account.setRole(role, true);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void prepare(boolean bot, SeededRng random) {
        var account = bot ? new BotAccount() : new HumanAccount();
        var username = random.nextUsername();
        assertThat(account.username(username)).isSameAs(account);
        account.prepare();
        assertThat(account.canonicalUsername()).isEqualTo(username.canonicalForm());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void prepare_ToleratesNullUsername(boolean bot) {
        // We want this to fail during validation so we get a clear error.
        var account = bot ? new BotAccount() : new HumanAccount();
        assertThatCode(account::prepare).doesNotThrowAnyException();
    }

}
