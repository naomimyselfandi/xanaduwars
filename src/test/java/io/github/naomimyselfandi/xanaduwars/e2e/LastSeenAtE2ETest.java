package io.github.naomimyselfandi.xanaduwars.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = """
        xanadu.last-seen-at.update-interval=20ms
        """)
class LastSeenAtE2ETest extends AbstractE2ETest {

    @AutoUser
    private TestUser user1;

    @AutoUser
    private TestUser user2;

    @Test
    void getAccount() throws Exception {
        as(user1)
                .perform(get("/account/{id}", user1.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastSeenAt", is(closeTo(Duration.ofSeconds(1), Instant.EPOCH))));
        Thread.sleep(Duration.ofMillis(100));
        as(user2)
                .perform(get("/account/{id}", user1.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastSeenAt", is(closeToNow(Duration.ofSeconds(1)))));
        as(user1)
                .perform(get("/account/{id}", user1.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastSeenAt", is(closeToNow(Duration.ofSeconds(1)))));
    }

}
