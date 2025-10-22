package io.github.naomimyselfandi.xanaduwars.e2e;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerE2ETest extends AbstractE2ETest {

    @AutoUser
    private TestUser regularUser;

    @Test
    void me() throws Exception {
        as(regularUser)
                .perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(regularUser.id().toString())))
                .andExpect(jsonPath("$.username", is(regularUser.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(regularUser.emailAddress().toString())))
                .andExpect(jsonPath("$.roles", is(empty())))
                .andExpect(jsonPath("$.rememberMe", is(false)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void me_WhenNotLoggedIn_Then401() throws Exception {
        as(null).perform(get("/auth/me")).andExpect(status().isNoContent());
    }

    @Test
    void me_ReportsRoles() throws Exception {
        as(rootUser)
                .perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(rootUser.id().toString())))
                .andExpect(jsonPath("$.username", is(rootUser.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(rootUser.emailAddress().toString())))
                .andExpect(jsonPath("$.roles", is(contains("ADMIN"))))
                .andExpect(jsonPath("$.rememberMe", is(false)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void login(boolean rememberMe) throws Exception {
        var token = as(null)
                .perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s",
                                  "rememberMe": %s
                                }
                                """.formatted(regularUser.username(), regularUser.password().text(), rememberMe)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().secure("refresh_token", true))
                .andExpect(cookie().sameSite("refresh_token", "Strict"))
                .andExpect(cookie().path("refresh_token", "/auth/refresh"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .transform(it -> (String) JsonPath.read(it, "$.accessToken"));
        usingToken(token)
                .perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(regularUser.id().toString())))
                .andExpect(jsonPath("$.username", is(regularUser.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(regularUser.emailAddress().toString())))
                .andExpect(jsonPath("$.roles", is(empty())))
                .andExpect(jsonPath("$.rememberMe", is(rememberMe)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void refresh(boolean rememberMe) throws Exception {
        var cookie = as(null)
                .perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s",
                                  "rememberMe": %s
                                }
                                """.formatted(regularUser.username(), regularUser.password().text(), rememberMe)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().secure("refresh_token", true))
                .andExpect(cookie().sameSite("refresh_token", "Strict"))
                .andExpect(cookie().path("refresh_token", "/auth/refresh"))
                .andReturn()
                .getResponse()
                .getCookie("refresh_token");
        assertThat(cookie).isNotNull();
        var token = as(null)
                .perform(post("/auth/refresh").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().secure("refresh_token", true))
                .andExpect(cookie().sameSite("refresh_token", "Strict"))
                .andExpect(cookie().path("refresh_token", "/auth/refresh"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .transform(it -> (String) JsonPath.read(it, "$.accessToken"));
        usingToken(token)
                .perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(regularUser.id().toString())))
                .andExpect(jsonPath("$.username", is(regularUser.username().toString())))
                .andExpect(jsonPath("$.emailAddress", is(regularUser.emailAddress().toString())))
                .andExpect(jsonPath("$.roles", is(empty())))
                .andExpect(jsonPath("$.rememberMe", is(rememberMe)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void logout() throws Exception {
        var token = as(null)
                .perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s",
                                  "rememberMe": false
                                }
                                """.formatted(regularUser.username(), regularUser.password().text())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .transform(it -> (String) JsonPath.read(it, "$.accessToken"));
        usingToken(token)
                .perform(post("/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().secure("refresh_token", true))
                .andExpect(cookie().sameSite("refresh_token", "Strict"))
                .andExpect(cookie().path("refresh_token", "/auth/refresh"))
                .andExpect(cookie().value("refresh_token", is("")));
    }

}
