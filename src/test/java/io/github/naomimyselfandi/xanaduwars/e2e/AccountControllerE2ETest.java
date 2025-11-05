package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.http.MediaType;

import java.time.Duration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerE2ETest extends AbstractE2ETest {

    @AutoUser
    private TestUser regularUser;

    @Test
    void getAccount() throws Exception {
        var id = rootUser.id();
        as(regularUser)
                .perform(get("/account/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.username", is(rootUser.username().toString())))
                .andExpect(jsonPath("$.roles", is(contains("ADMIN"))))
                .andExpect(jsonPath("$.createdAt", is(closeToNow(Duration.ofMinutes(3)))))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.rememberMe").doesNotExist())
                .andExpect(jsonPath("$._links.self.href", endsWith("/account/" + id)))
                .andExpect(jsonPath("$._links.roles.href").doesNotExist());
    }

    @Test
    void getAccount_AsAdmin() throws Exception {
        var id = rootUser.id();
        as(rootUser)
                .perform(get("/account/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.username", is(rootUser.username().toString())))
                .andExpect(jsonPath("$.roles", is(contains("ADMIN"))))
                .andExpect(jsonPath("$.createdAt", is(closeToNow(Duration.ofMinutes(3)))))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.rememberMe").doesNotExist())
                .andExpect(jsonPath("$._links.self.href", endsWith("/account/" + id)))
                .andExpect(jsonPath("$._links.roles.href", endsWith("/account/" + id + "/roles")));
    }

    @Test
    void updateRoles(SeededRng random) throws Exception {
        var role = random.<Role>get();
        as(rootUser)
                .perform(patch("/account/{id}/roles", regularUser.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"%s\"]".formatted(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(regularUser.id().toString())))
                .andExpect(jsonPath("$.username", is(regularUser.username().toString())))
                .andExpect(jsonPath("$.roles", is(contains(role.toString()))))
                .andExpect(jsonPath("$.createdAt", is(closeToNow(Duration.ofMinutes(3)))))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.rememberMe").doesNotExist());
        as(regularUser)
                .perform(get("/account/{id}", regularUser.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(regularUser.id().toString())))
                .andExpect(jsonPath("$.username", is(regularUser.username().toString())))
                .andExpect(jsonPath("$.roles", is(contains(role.toString()))))
                .andExpect(jsonPath("$.createdAt", is(closeToNow(Duration.ofMinutes(3)))))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.rememberMe").doesNotExist());
    }

    @NullSource
    @ParameterizedTest
    @EnumSource(names = "ADMIN", mode = EnumSource.Mode.EXCLUDE)
    void updateRoles_OnlyAdminsCanChangeRoles(@Nullable Role role, SeededRng random) throws Exception {
        as(createAccount(role))
                .perform(patch("/account/{id}/roles", regularUser.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"%s\"]".formatted(random.<Role>get())))
                .andExpect(status().isForbidden());
        as(regularUser)
                .perform(get("/account/{id}", regularUser.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(regularUser.id().toString())))
                .andExpect(jsonPath("$.username", is(regularUser.username().toString())))
                .andExpect(jsonPath("$.roles", is(empty())))
                .andExpect(jsonPath("$.createdAt", is(closeToNow(Duration.ofMinutes(3)))))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.emailAddress").doesNotExist())
                .andExpect(jsonPath("$.rememberMe").doesNotExist());
    }

}
