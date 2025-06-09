package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "xanadu.core.ruleset.root=testRuleset")
public class InfoControllerE2ETest extends AbstractE2ETest {

    @Test
    void canListVersions() throws Exception {
        query("/info/version")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("1.2.3")))
                .andExpect(jsonPath("$[1]", is("1.2.2")));
    }

    @Test @Login(roles = Role.DEVELOPER)
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

    @Test @Login(roles = Role.DEVELOPER)
    void canListVersions_WhenADeveloperUsesTheNormalEndpoint_OnlyPublicVersionsAreListed() throws Exception {
        canListVersions();
    }

    @Test @Login
    void regularUsersCannotListAllVersions() throws Exception {
        query("/info/version/internal")
                .get()
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    void unauthenticatedUsersCannotListAllVersions() throws Exception {
        query("/info/version/internal")
                .get()
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3", "1.2.2"})
    void canGetRuleset(String versionNumber) throws Exception {
        query("/info/ruleset/{version}", versionNumber)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commanders", hasSize(2)))
                .andExpect(jsonPath("$.commanders[0].name", is("Alice")))
                .andExpect(jsonPath("$.commanders[0].affinities", aMapWithSize(2)))
                .andExpect(jsonPath("$.commanders[0].affinities.Offensive", is("POSITIVE")))
                .andExpect(jsonPath("$.commanders[0].affinities.Defensive", is("NEGATIVE")))
                .andExpect(jsonPath("$.commanders[0].signatureSpells", is(List.of("MagicMissile"))))
                .andExpect(jsonPath("$.commanders[1].name", is("Bob")))
                .andExpect(jsonPath("$.commanders[1].affinities", aMapWithSize(2)))
                .andExpect(jsonPath("$.commanders[1].affinities.Offensive", is("NEGATIVE")))
                .andExpect(jsonPath("$.commanders[1].affinities.Defensive", is("POSITIVE")))
                .andExpect(jsonPath("$.commanders[1].signatureSpells", is(List.of("QuickFix"))))
                .andExpect(jsonPath("$.structureTypes", hasSize(6)))
                .andExpect(jsonPath("$.structureTypes[0].name", is("Headquarters")))
                .andExpect(jsonPath("$.structureTypes[1].name", is("Depot")))
                .andExpect(jsonPath("$.structureTypes[2].name", is("Tower")))
                .andExpect(jsonPath("$.structureTypes[3].name", is("Fort")))
                .andExpect(jsonPath("$.structureTypes[4].name", is("Port")))
                .andExpect(jsonPath("$.structureTypes[5].name", is("Airfield")))
                .andExpect(jsonPath("$.tileTypes", hasSize(17)))
                .andExpect(jsonPath("$.tileTypes[0].name", is("Plains")))
                .andExpect(jsonPath("$.tileTypes[0].movementTable.Treaded", is(1.0)))
                .andExpect(jsonPath("$.tileTypes[0].movementTable.Wheeled", is(1.5)))
                .andExpect(jsonPath("$.tileTypes[0].movementTable.Soldier", is(1.0)))
                .andExpect(jsonPath("$.tileTypes[0].movementTable.Air", is(1.0)))
                .andExpect(jsonPath("$.tileTypes[0].cover", is(0.1)))
                .andExpect(jsonPath("$.tileTypes[0].tags", is(List.of())))
                .andExpect(jsonPath("$.tileTypes[1].name", is("Road")))
                .andExpect(jsonPath("$.tileTypes[1].movementTable.Treaded", is(1.0)))
                .andExpect(jsonPath("$.tileTypes[1].movementTable.Wheeled", is(1.0)))
                .andExpect(jsonPath("$.tileTypes[1].movementTable.Soldier", is(0.75)))
                .andExpect(jsonPath("$.tileTypes[1].movementTable.Air", is(1.0)))
                .andExpect(jsonPath("$.tileTypes[1].cover", is(0.0)))
                .andExpect(jsonPath("$.tileTypes[1].tags", is(List.of())))
                .andExpect(jsonPath("$.tileTypes[4].name", is("Ruins")))
                .andExpect(jsonPath("$.tileTypes[4].movementTable.Land", is(1.0)))
                .andExpect(jsonPath("$.tileTypes[4].movementTable.Air", is(1.0)))
                .andExpect(jsonPath("$.tileTypes[4].cover", is(0.2)))
                .andExpect(jsonPath("$.tileTypes[4].tags", is(List.of("HidingSpot"))))
                .andExpect(jsonPath("$.unitTypes", hasSize(19)))
                .andExpect(jsonPath("$.unitTypes[0].name", is("Engineer")))
                .andExpect(jsonPath("$.unitTypes[0].tags", containsInAnyOrder("Soldier", "Land")))
                .andExpect(jsonPath("$.unitTypes[0].speed", is(4)))
                .andExpect(jsonPath("$.unitTypes[0].vision", is(2)))
                .andExpect(jsonPath("$.unitTypes[0].supplyCost", is(100)))
                .andExpect(jsonPath("$.unitTypes[0].aetherCost", is(0)))
                .andExpect(jsonPath("$.unitTypes[0].weapons", hasSize(1)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].name", is("Rifle")))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].damage.Engineer", is(40)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].damage.RifleSquad", is(20)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].damage.RepeaterSquad", is(5)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].damage.HavocSquad", is(20)))
                .andExpect(jsonPath("$.unitTypes[0].hangar", is(List.of())))
                .andExpect(jsonPath("$.unitTypes[11].name", is("Ornithopter")))
                .andExpect(jsonPath("$.unitTypes[11].tags", is(List.of("Air"))))
                .andExpect(jsonPath("$.unitTypes[11].speed", is(6)))
                .andExpect(jsonPath("$.unitTypes[11].vision", is(4)))
                .andExpect(jsonPath("$.unitTypes[11].supplyCost", is(300)))
                .andExpect(jsonPath("$.unitTypes[11].aetherCost", is(20)))
                .andExpect(jsonPath("$.unitTypes[11].weapons", hasSize(0)))
                .andExpect(jsonPath("$.unitTypes[16].name", is("Corvette")))
                .andExpect(jsonPath("$.unitTypes[16].tags", containsInAnyOrder("Naval", "Ship")))
                .andExpect(jsonPath("$.unitTypes[16].speed", is(6)))
                .andExpect(jsonPath("$.unitTypes[16].vision", is(2)))
                .andExpect(jsonPath("$.unitTypes[16].supplyCost", is(600)))
                .andExpect(jsonPath("$.unitTypes[16].aetherCost", is(60)))
                .andExpect(jsonPath("$.unitTypes[16].weapons", hasSize(2)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].maximumRange", is(2)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].minimumRange", is(0)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].maximumRange", is(1)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].minimumRange", is(0)));
    }

    @ParameterizedTest @Login(roles = Role.DEVELOPER)
    @ValueSource(strings = {"1.2.3-Foo", "1.2.3", "1.2.2-Foo", "1.2.2"})
    void developersCanGetInternalRulesets(String versionNumber) throws Exception {
        canGetRuleset(versionNumber);
    }

}
