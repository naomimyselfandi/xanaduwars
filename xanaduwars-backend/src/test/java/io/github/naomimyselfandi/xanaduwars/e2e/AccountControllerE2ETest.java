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
        query("/account/{ref}", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId().toString())))
                .andExpect(jsonPath("$.username", is(account.getUsername().toString())))
                .andExpect(jsonPath("$.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.settings").doesNotExist());
    }

    @Login
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void canGetFullInfoAboutSelf(boolean byId) throws Exception {
        query("/account/{ref}/full", byId ? account.getId() : "me")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId().toString())))
                .andExpect(jsonPath("$.username", is(account.getUsername().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.getEmailAddress().toString())))
                .andExpect(jsonPath("$.settings.timezone", is("UTC")));
    }

    @Test
    @Login
    void regularAccountsCannotGetFullInfoAboutOtherAccounts() throws Exception {
        var account = createAccount(AccountKind.HUMAN);
        query("/account/{ref}/full", account.getId())
                .get()
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    @Login(roles = Role.SUPPORT)
    void supportAccountsCanGetFullInfoAboutOtherAccounts() throws Exception {
        var account = createAccount(AccountKind.HUMAN);
        query("/account/{ref}/full", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId().toString())))
                .andExpect(jsonPath("$.username", is(account.getUsername().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.getEmailAddress().toString())))
                .andExpect(jsonPath("$.settings.timezone", is("UTC")));
    }

}
