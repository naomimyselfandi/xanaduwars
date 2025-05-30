package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CurrentAccountControllerE2ETest extends AbstractE2ETest {

    @RepeatedTest(2) @WithTestUser
    void getCurrentAccount() throws Exception {
        query("/account/me")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.id().toString())))
                .andExpect(jsonPath("$.username", is(account.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.emailAddress().toString())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.apiKey").doesNotExist())
                .andExpect(jsonPath("$.admin", is(false)))
                .andExpect(jsonPath("$.moderator", is(false)))
                .andExpect(jsonPath("$.judge", is(false)))
                .andExpect(jsonPath("$.support", is(false)))
                .andExpect(jsonPath("$.developer", is(false)))
                .andExpect(jsonPath("$.settings.timezone", is("UTC")));
    }

    @Test @WithTestUser(kind = AccountKind.BOT)
    void getCurrentAccount_AsBot() throws Exception {
        getCurrentAccount();
    }

    @Test @WithTestUser(roles = Role.ADMIN)
    void getCurrentAccount_AsAdmin() throws Exception {
        query("/account/me")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.id().toString())))
                .andExpect(jsonPath("$.username", is(account.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.emailAddress().toString())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.apiKey").doesNotExist())
                .andExpect(jsonPath("$.admin", is(true)))
                .andExpect(jsonPath("$.moderator", is(true)))
                .andExpect(jsonPath("$.judge", is(true)))
                .andExpect(jsonPath("$.support", is(true)))
                .andExpect(jsonPath("$.developer", is(true)));
    }

    @Test @WithTestUser(roles = Role.DEVELOPER)
    void getCurrentAccount_AsDeveloper() throws Exception {
        query("/account/me")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.id().toString())))
                .andExpect(jsonPath("$.username", is(account.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.emailAddress().toString())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.apiKey").doesNotExist())
                .andExpect(jsonPath("$.admin", is(false)))
                .andExpect(jsonPath("$.moderator", is(false)))
                .andExpect(jsonPath("$.judge", is(false)))
                .andExpect(jsonPath("$.support", is(true)))
                .andExpect(jsonPath("$.developer", is(true)));
    }

}
