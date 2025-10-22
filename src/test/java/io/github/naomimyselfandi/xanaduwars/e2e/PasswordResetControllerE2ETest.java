package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Plaintext;
import io.github.naomimyselfandi.xanaduwars.email.value.PasswordResetContent;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PasswordResetControllerE2ETest extends AbstractE2ETest {

    @AutoUser
    private TestUser testUser;

    @Test
    void passwordReset(SeededRng random) throws Exception {
        var password = random.<Plaintext>get();
        as(null)
                .perform(post("/auth/passwordReset")
                        .queryParam("emailAddress", testUser.emailAddress().toString()))
                .andExpect(status().isNoContent());
        var token = getEmails(testUser.emailAddress(), PasswordResetContent.class)
                .stream()
                .findFirst()
                .orElseGet(Assertions::fail)
                .token()
                .token();
        as(null)
                .perform(get("/auth/passwordReset/{token}", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.id().toString())))
                .andExpect(jsonPath("$.username", is(testUser.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(testUser.emailAddress().toString())))
                .andExpect(jsonPath("$.roles", is(empty())))
                .andExpect(jsonPath("$.rememberMe", is(false)))
                .andExpect(jsonPath("$.password").doesNotExist());
        as(null)
                .perform(post("/auth/passwordReset/{token}", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "%s"
                                }
                                """.formatted(password.text()))
                )
                .andExpect(status().isNoContent());
        testUser = testUser.toBuilder().password(password).build();
        as(testUser)
                .perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.id().toString())))
                .andExpect(jsonPath("$.username", is(testUser.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(testUser.emailAddress().toString())))
                .andExpect(jsonPath("$.roles", is(empty())))
                .andExpect(jsonPath("$.rememberMe", is(false)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

}
