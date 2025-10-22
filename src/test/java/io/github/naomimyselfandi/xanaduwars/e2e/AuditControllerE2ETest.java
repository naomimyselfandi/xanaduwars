package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.Duration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuditControllerE2ETest extends AbstractE2ETest {

    @AutoUser(Role.MODERATOR)
    private TestUser moderator;

    @AutoUser
    private TestUser regularUser;

    @Test
    void adminsCanReadAuditLogs() throws Exception {
        readLog(rootUser);
    }

    @Test
    void moderatorsCanReadAuditLogs() throws Exception {
        readLog(moderator);
    }

    private void readLog(TestUser user) throws Exception {
        as(user)
                .perform(get("/audit/testHelper/ping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"pong\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("pong")));
        as(user)
                .perform(get("/audit")
                        .queryParam("accountId", user.id().toString())
                        .queryParam("action", "PING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isString())
                .andExpect(jsonPath("$.content[0].timestamp", is(closeToNow(Duration.ofSeconds(3)))))
                .andExpect(jsonPath("$.content[0].accountId", is(user.id().toString())))
                .andExpect(jsonPath("$.content[0].username", is(user.username().toString())))
                .andExpect(jsonPath("$.content[0].httpMethod", is("GET")))
                .andExpect(jsonPath("$.content[0].httpPath", is("/audit/testHelper/ping")))
                .andExpect(jsonPath("$.content[0].httpQuery", is("")))
                .andExpect(jsonPath("$.content[0].httpBody", is("\"pong\"")))
                .andExpect(jsonPath("$.content[0].action", is("PING")))
                .andExpect(jsonPath("$.content[0].sourceClass", is("i.g.n.x.e.AuditControllerE2EHelper")))
                .andExpect(jsonPath("$.content[0].sourceMethod", is("ping")))
                .andExpect(jsonPath("$.page.size", is(10)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$.page.totalElements", is(1)))
                .andExpect(jsonPath("$.page.totalPages", is(1)));
    }

    @Test
    void regularUsersCannotReadAuditLogs() throws Exception {
        as(regularUser)
                .perform(get("/audit"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpect(jsonPath("$.page").doesNotExist());
        as(moderator)
                .perform(get("/audit").queryParam("accountId", regularUser.id().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(10)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$.page.totalElements", is(0)))
                .andExpect(jsonPath("$.page.totalPages", is(0)));
    }

}
