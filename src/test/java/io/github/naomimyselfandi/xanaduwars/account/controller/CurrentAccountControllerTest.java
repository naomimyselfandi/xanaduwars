package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class CurrentAccountControllerTest {

    private CurrentAccountController fixture;

    @BeforeEach
    void setup() {
        fixture = new CurrentAccountController();
    }

    @Test
    void me(SeededRng random) {
        var account = new HumanAccount().id(random.nextUUID()).username(random.nextUsername());
        assertThat(fixture.me(account)).isEqualTo(ResponseEntity.ok(new FullAccountDto(account)));
    }

}
