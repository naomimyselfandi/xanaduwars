package io.github.naomimyselfandi.xanaduwars.integration;

import com.jayway.jsonpath.JsonPath;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTDurationService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @RepeatedTest(3)
    void testRegistrationAndLogin(SeededRng random) throws Exception {
        var username = random.nextUsername();
        var email = random.nextEmailAddress();
        var plaintext = random.nextPlaintextPassword();
        var rememberMe = random.pick(JWTDurationService.RememberMe.values());
        mockMvc.perform(get("/account/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("rememberMe", rememberMe.toString())
                        .content("""
                        {"username": "%s", "password": "%s"}
                        """.formatted(username, plaintext)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/account/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username": "%s", "emailAddress": "%s", "password": "%s"}
                        """.formatted(username, email, plaintext)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/account/me")).andExpect(status().isUnauthorized());
        var token = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("rememberMe", rememberMe.toString())
                        .content("""
                        {"username": "%s", "password": "%s"}
                        """.formatted(username, plaintext)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().secure("refresh_token", true))
                .andExpect(cookie().sameSite("refresh_token", "Strict"))
                .andExpect(cookie().path("refresh_token", "/auth/refresh"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .transform(body -> JsonPath.read(body, "$.accessToken"));
        mockMvc.perform(get("/account/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @RepeatedTest(3)
    void testRefreshAndLogout(SeededRng random) throws Exception {
        var username = random.nextUsername();
        var email = random.nextEmailAddress();
        var plaintext = random.nextPlaintextPassword();
        var rememberMe = random.pick(JWTDurationService.RememberMe.values());
        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"username": "%s", "emailAddress": "%s", "password": "%s"}
                        """.formatted(username, email, plaintext)))
                .andExpect(status().isNoContent());
        var refreshToken = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("rememberMe", rememberMe.toString())
                        .content("""
                        {"username": "%s", "password": "%s"}
                        """.formatted(username, plaintext)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andReturn()
                .getResponse()
                .getCookie("refresh_token");
        assertThat(refreshToken).isNotNull();
        var token = mockMvc.perform(post("/auth/refresh")
                        .queryParam("rememberMe", rememberMe.toString())
                        .cookie(refreshToken))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().secure("refresh_token", true))
                .andExpect(cookie().value("refresh_token", not(is(refreshToken.getValue()))))
                .andExpect(cookie().sameSite("refresh_token", "Strict"))
                .andExpect(cookie().path("refresh_token", "/auth/refresh"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .transform(body -> JsonPath.read(body, "$.accessToken"));
        mockMvc.perform(get("/account/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/auth"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().value("refresh_token", ""));
    }

}
