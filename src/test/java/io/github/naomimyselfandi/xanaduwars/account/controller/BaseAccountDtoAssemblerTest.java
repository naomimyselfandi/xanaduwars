package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.security.hateoas.HateoasTest;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.naomimyselfandi.xanaduwars.security.hateoas.Faker.$;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class BaseAccountDtoAssemblerTest extends HateoasTest {

    private BaseAccountDtoAssembler fixture;

    private BaseAccountDto dto;

    @BeforeEach
    void setup(SeededRng random) {
        fixture = new BaseAccountDtoAssembler();
        dto = random.get();
    }

    @Test
    void self() {
        assertQuery(dto, fixture::self, controller -> controller.get(dto.id()));
    }

    @Test
    void roles() {
        assertQuery(dto, fixture::roles, controller -> controller.updateRoles(dto.id(), $()));
    }

}
