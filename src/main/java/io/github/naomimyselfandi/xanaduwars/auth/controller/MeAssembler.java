package io.github.naomimyselfandi.xanaduwars.auth.controller;

import io.github.naomimyselfandi.xanaduwars.account.controller.AccountController;
import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.security.hateoas.SecurityAwareAssembler;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static io.github.naomimyselfandi.xanaduwars.security.hateoas.Faker.$;

@Component
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
class MeAssembler extends SecurityAwareAssembler<Optional<UserDetailsDto>> {

    @Link
    public Object self(AuthController controller, Optional<UserDetailsDto> dto) {
        return controller.me(dto);
    }

    @Link
    public @Nullable Object login(AuthController controller, Optional<UserDetailsDto> dto) {
        return dto.isPresent() ? null : controller.login($(), $());
    }

    @Link
    public @Nullable Object register(AuthController controller, Optional<UserDetailsDto> dto) {
        return dto.isPresent() ? null : controller.register($());
    }

    @Link
    public @Nullable Object logout(AuthController controller, Optional<UserDetailsDto> dto) {
        return dto.isPresent() ? controller.logout($()) : null;
    }

    @Link
    public @Nullable Object details(AccountController controller, Optional<UserDetailsDto> dto) {
        return dto.map(UserDetailsDto::id).map(controller::get).orElse(null);
    }

}
