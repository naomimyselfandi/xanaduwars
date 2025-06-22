package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.dto.FullAccountDto;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerE2ETest extends BaseE2ETest {

    @E2ETest(repetitions = 5)
    void canGetBasicInfo(SeededRng random) throws Exception {
        var account = createAccount(random.shuffle(Role.values()).subList(0, 3).toArray(Role[]::new));
        query("/account/{ref}", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId().toString())))
                .andExpect(jsonPath("$.username", is(account.getUsername().toString())))
                .andExpect(jsonPath("$.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.settings").doesNotExist())
                .andExpect(jsonPath("$.support", is(account.isSupport())))
                .andExpect(jsonPath("$.moderator", is(account.isModerator())))
                .andExpect(jsonPath("$.judge", is(account.isJudge())))
                .andExpect(jsonPath("$.admin", is(account.isAdmin())))
                .andExpect(jsonPath("$.developer", is(account.isDeveloper())))
                .andExpect(jsonPath("$.bot", is(account.isBot())))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.lastSeenAt", notNullValue()));
    }

    @E2ETest
    @E2ETest(roles = Role.BOT)
    @E2ETest(roles = Role.SUPPORT)
    @E2ETest(roles = {Role.SUPPORT, Role.MODERATOR})
    @E2ETest(roles = Role.ADMIN)
    void canGetFullInfoAboutSelfById() throws Exception {
        query("/account/me", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId().toString())))
                .andExpect(jsonPath("$.username", is(account.getUsername().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.getEmailAddress().toString())))
                .andExpect(jsonPath("$.settings.timezone", is("UTC")))
                .andExpect(jsonPath("$.support", is(account.isSupport())))
                .andExpect(jsonPath("$.moderator", is(account.isModerator())))
                .andExpect(jsonPath("$.judge", is(account.isJudge())))
                .andExpect(jsonPath("$.admin", is(account.isAdmin())))
                .andExpect(jsonPath("$.developer", is(account.isDeveloper())))
                .andExpect(jsonPath("$.bot", is(account.isBot())));
    }

    @E2ETest(roles = Role.SUPPORT)
    @E2ETest(roles = Role.ADMIN)
    void supportAccountsCanGetFullInfoAboutOtherAccounts() throws Exception {
        var account = createAccount();
        query("/account/{ref}/full", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId().toString())))
                .andExpect(jsonPath("$.username", is(account.getUsername().toString())))
                .andExpect(jsonPath("$.emailAddress", is(account.getEmailAddress().toString())))
                .andExpect(jsonPath("$.settings.timezone", is("UTC")));
    }

    @E2ETest
    void regularAccountsCannotGetFullInfoAboutOtherAccounts() throws Exception {
        var account = createAccount();
        query("/account/{ref}/full", account.getId())
                .get()
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @E2ETest
    void canHideActivity() throws Exception {
        var settings = query("/account/me")
                .extractingBody(HttpStatus.OK)
                .as(FullAccountDto.class)
                .get()
                .getSettings();
        settings.setHideActivity(true);
        query("/account/me/settings")
                .withContent(settings)
                .patch()
                .andExpect(status().isOk());
        query("/account/me")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastSeenAt", notNullValue()));
        var accountId = account.getId();
        login(createAccount());
        query("/account/{id}", accountId)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastSeenAt", nullValue()));
    }

}
