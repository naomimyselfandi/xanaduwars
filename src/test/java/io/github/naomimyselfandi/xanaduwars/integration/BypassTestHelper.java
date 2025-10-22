package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.security.Bypassable;
import org.springframework.stereotype.Service;

@Service
class BypassTestHelper {

    @Bypassable(Role.MODERATOR)
    boolean testMethod() {
        return false;
    }

}
