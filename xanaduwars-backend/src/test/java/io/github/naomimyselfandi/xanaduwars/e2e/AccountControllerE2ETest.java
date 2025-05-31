package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerE2ETest extends AbstractE2ETest {

    @Test
    @Login
    void canGetBasicInfo() throws Exception {
        var account = createAccount(AccountKind.HUMAN);
        query("/account/{ref}", account.id())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.id().toString())))
                .andExpect(jsonPath("$.username", is(account.username().toString())))
                .andExpect(jsonPath("$.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.settings").doesNotExist());
    }

    @Login
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void canGetFullInfoAboutSelf(boolean byId) throws Exception {
        query("/account/{ref}/full", byId ? account.id() : "me")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.id().toString())))
                .andExpect(jsonPath("$.username", is(account.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.emailAddress().toString())))
                .andExpect(jsonPath("$.settings.timezone", is("UTC")));
    }

    @Test
    @Login
    void regularAccountsCannotGetFullInfoAboutOtherAccounts() throws Exception {
        var account = createAccount(AccountKind.HUMAN);
        query("/account/{ref}/full", account.id())
                .get()
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    @Login(roles = Role.SUPPORT)
    void supportAccountsCanGetFullInfoAboutOtherAccounts() throws Exception {
        var account = createAccount(AccountKind.HUMAN);
        query("/account/{ref}/full", account.id())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.id().toString())))
                .andExpect(jsonPath("$.username", is(account.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.emailAddress().toString())))
                .andExpect(jsonPath("$.settings.timezone", is("UTC")));
    }

}
