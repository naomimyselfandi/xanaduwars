package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.security.hateoas.HateoasTest;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static io.github.naomimyselfandi.xanaduwars.security.hateoas.Faker.$;

@ExtendWith(SeededRandomExtension.class)
class MeAssemblerTest extends HateoasTest {

    private MeAssembler fixture;

    private UserDetailsDto dto;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new MeAssembler();
        dto = random.get();
    }

    @Test
    void self() {
        assertQuery(Optional.of(dto), fixture::self, controller -> controller.me(Optional.of(dto)));
        assertQuery(Optional.empty(), fixture::self, controller -> controller.me(Optional.empty()));
    }

    @Test
    void login() {
        assertQuery(Optional.empty(), fixture::login, controller -> controller.login($(), $()));
    }

    @Test
    void login_WhenAlreadyLoggedIn_ThenNull() {
        assertQueryIsNull(Optional.of(dto), fixture::login);
    }

    @Test
    void register() {
        assertQuery(Optional.empty(), fixture::register, controller -> controller.register($()));
    }

    @Test
    void register_WhenAlreadyLoggedIn_ThenNull() {
        assertQueryIsNull(Optional.of(dto), fixture::register);
    }

    @Test
    void logout() {
        assertQuery(Optional.of(dto), fixture::logout, controller -> controller.logout($()));
    }

    @Test
    void logout_WhenNotLoggedIn_ThenNull() {
        assertQueryIsNull(Optional.empty(), fixture::logout);
    }

    @Test
    void details() {
        assertQuery(Optional.of(dto), fixture::details, controller -> controller.get(dto.id()));
    }

    @Test
    void details_WhenNotLoggedIn_ThenNull() {
        assertQueryIsNull(Optional.empty(), fixture::details);
    }

}
