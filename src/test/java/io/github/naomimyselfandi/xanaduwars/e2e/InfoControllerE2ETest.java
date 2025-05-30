package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "xanadu.core.ruleset.root=infoControllerTest")
public class InfoControllerE2ETest extends AbstractE2ETest {

    @Test
    void infoEndpointsAreAvailableWithoutAuthorization() throws Exception {
        query("/info/ping")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Pong!")));
    }

    @Test
    void canListVersions() throws Exception {
        query("/info/version")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("1.2.3")))
                .andExpect(jsonPath("$[1]", is("1.2.2")));
    }

    @Test @WithTestUser(roles = Role.DEVELOPER)
    void canListVersions_DevelopersCanListAlLVersions() throws Exception {
        query("/info/version/internal")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0]", is("1.2.3-Foo")))
                .andExpect(jsonPath("$[1]", is("1.2.3")))
                .andExpect(jsonPath("$[2]", is("1.2.2-Foo")))
                .andExpect(jsonPath("$[3]", is("1.2.2")));
    }

    @Test @WithTestUser(roles = Role.DEVELOPER)
    void canListVersions_WhenADeveloperUsesTheNormalEndpoint_OnlyPublicVersionsAreListed() throws Exception {
        canListVersions();
    }

    @Test @WithTestUser
    void regularUsersCannotListAllVersions() throws Exception {
        query("/info/version/internal")
                .get()
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    void unauthenticatedUsersCannotListAllVersions() throws Exception {
        regularUsersCannotListAllVersions();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3", "1.2.2"})
    void canGetRuleset(String versionNumber) throws Exception {
        query("/info/ruleset/{version}", versionNumber)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is(versionNumber)))
                .andExpect(jsonPath("$.commanders", hasSize(1)))
                .andExpect(jsonPath("$.spellTypes", hasSize(3)))
                .andExpect(jsonPath("$.tileTypes", hasSize(24)))
                .andExpect(jsonPath("$.unitTypes", hasSize(13)));
    }

    @ParameterizedTest @WithTestUser(roles = Role.DEVELOPER)
    @ValueSource(strings = {"1.2.3-Foo", "1.2.3", "1.2.2-Foo", "1.2.2"})
    void developersCanGetInternalRulesets(String versionNumber) throws Exception {
        canGetRuleset(versionNumber);
    }

}
