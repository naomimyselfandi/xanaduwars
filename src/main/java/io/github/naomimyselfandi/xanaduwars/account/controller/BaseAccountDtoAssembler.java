package io.github.naomimyselfandi.xanaduwars.account.controller;

import io.github.naomimyselfandi.xanaduwars.account.dto.BaseAccountDto;
import io.github.naomimyselfandi.xanaduwars.security.hateoas.SecurityAwareAssembler;
import org.springframework.stereotype.Component;

import static io.github.naomimyselfandi.xanaduwars.security.hateoas.Faker.$;

@Component
class BaseAccountDtoAssembler extends SecurityAwareAssembler<BaseAccountDto> {

    @Link
    public Object self(AccountController controller, BaseAccountDto dto) {
        return controller.get(dto.id());
    }

    @Link
    public Object roles(AccountController controller, BaseAccountDto dto) {
        return controller.updateRoles(dto.id(), $());
    }

}
