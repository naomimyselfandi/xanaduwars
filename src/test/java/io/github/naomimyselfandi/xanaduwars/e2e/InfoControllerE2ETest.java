package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "xanadu.core.ruleset.root=testRuleset")
public class InfoControllerE2ETest extends BaseE2ETest {

    @E2ETest
    @E2ETest(false)
    @E2ETest(roles = Role.DEVELOPER)
    @E2ETest(roles = Role.ADMIN)
    void canListPublishedVersions() throws Exception {
        query("/info/version")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("1.2.3")))
                .andExpect(jsonPath("$[1]", is("0.0.0")));
    }

    @E2ETest(roles = Role.DEVELOPER)
    @E2ETest(roles = Role.ADMIN)
    void developersCanListAllVersions() throws Exception {
        query("/info/version/internal")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0]", is("1.2.3")))
                .andExpect(jsonPath("$[1]", is("0.0.0-Foo")))
                .andExpect(jsonPath("$[2]", is("0.0.0-Bar")))
                .andExpect(jsonPath("$[3]", is("0.0.0")));
    }

    @E2ETest
    @E2ETest(false)
    @E2ETest(roles = Role.SUPPORT)
    void regularUsersCannotListAllVersions(E2ETest testCase) throws Exception {
        query("/info/version/internal")
                .get()
                .andExpect(status().is(testCase.value() ? 403 : 401))
                .andExpect(content().string(""));
    }

    @E2ETest(payload = "0.0.0")
    @E2ETest(payload = "1.2.3")
    void canGetPublishedRulesets(String version) throws Exception {
        query("/info/ruleset/{version}", version)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is(version)));
    }

    @E2ETest(payload = "0.0.0-Foo")
    @E2ETest(payload = "0.0.0-Bar")
    @E2ETest(value = false, payload = "0.0.0-Foo")
    @E2ETest(roles = Role.SUPPORT, payload = "0.0.0-Bar")
    void regularUsersCannotGetInternalRulesets(E2ETest testCase, String version) throws Exception {
        query("/info/ruleset/{version}", version)
                .get()
                .andExpect(status().is(testCase.value() ? 403 : 401))
                .andExpect(content().string(""));
    }

    @E2ETest(roles = Role.DEVELOPER, payload = "0.0.0")
    @E2ETest(roles = Role.DEVELOPER, payload = "0.0.0-Bar")
    @E2ETest(roles = Role.DEVELOPER, payload = "0.0.0-Foo")
    @E2ETest(roles = Role.DEVELOPER, payload = "1.2.3")
    void developersCanGetInternalRulesets(String version) throws Exception {
        query("/info/ruleset/{version}", version)
                .get()
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is(version)));
    }

    @E2ETest
    void canGetRulesetDetails() throws Exception {
        query("/info/ruleset/1.2.3")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("1.2.3")))
                .andExpect(jsonPath("$.actions[0].name", is("Move")))
                .andExpect(jsonPath("$.actions[0].targets", hasSize(1)))
                .andExpect(jsonPath("$.actions[0].targets[0].path", is(true)))
                .andExpect(jsonPath("$.actions[0].tags", empty()))
                .andExpect(jsonPath("$.actions[1].name", is("Load")))
                .andExpect(jsonPath("$.actions[1].targets", hasSize(1)))
                .andExpect(jsonPath("$.actions[1].targets[0].Structure").doesNotExist())
                .andExpect(jsonPath("$.actions[1].targets[0].Tile").doesNotExist())
                .andExpect(jsonPath("$.actions[1].targets[0].Unit", is(true)))
                .andExpect(jsonPath("$.actions[1].targets[0].Own", is(true)))
                .andExpect(jsonPath("$.actions[1].targets[0].Ally").doesNotExist())
                .andExpect(jsonPath("$.actions[1].targets[0].Enemy").doesNotExist())
                .andExpect(jsonPath("$.actions[1].targets[0].Neutral").doesNotExist())
                .andExpect(jsonPath("$.actions[1].targets[0].path", is(false)))
                .andExpect(jsonPath("$.actions[1].targets[0].minRange", is(1)))
                .andExpect(jsonPath("$.actions[1].targets[0].maxRange", is(1)))
                .andExpect(jsonPath("$.actions[1].tags", contains("FreeAction")))
                .andExpect(jsonPath("$.actions[4].name", is("RepairVehicle")))
                .andExpect(jsonPath("$.commanders", hasSize(2)))
                .andExpect(jsonPath("$.commanders[0].id", is(0)))
                .andExpect(jsonPath("$.commanders[0].name", is("Alice")))
                .andExpect(jsonPath("$.commanders[0].affinities", aMapWithSize(2)))
                .andExpect(jsonPath("$.commanders[0].affinities.Offensive", is("Positive")))
                .andExpect(jsonPath("$.commanders[0].affinities.Defensive", is("Negative")))
                .andExpect(jsonPath("$.commanders[0].signatureSpells", is(List.of("MagicMissile"))))
                .andExpect(jsonPath("$.commanders[0].chosenSpells", is(2)))
                .andExpect(jsonPath("$.commanders[1].id", is(1)))
                .andExpect(jsonPath("$.commanders[1].name", is("Bob")))
                .andExpect(jsonPath("$.commanders[1].affinities", aMapWithSize(2)))
                .andExpect(jsonPath("$.commanders[1].affinities.Offensive", is("Negative")))
                .andExpect(jsonPath("$.commanders[1].affinities.Defensive", is("Positive")))
                .andExpect(jsonPath("$.commanders[1].signatureSpells", is(List.of("QuickFix"))))
                .andExpect(jsonPath("$.commanders[1].chosenSpells", is(2)))
                .andExpect(jsonPath("$.spells", hasSize(8)))
                .andExpect(jsonPath("$.spells[0].id", is(0)))
                .andExpect(jsonPath("$.spells[0].name", is("MagicMissile")))
                .andExpect(jsonPath("$.spells[0].tags", contains("Offensive")))
                .andExpect(jsonPath("$.spells[0].focusCost", is(4000)))
                .andExpect(jsonPath("$.spells[0].signatureSpell", is(true)))
                .andExpect(jsonPath("$.spells[0].targets", hasSize(1)))
                .andExpect(jsonPath("$.spells[0].targets[0].Own").doesNotExist())
                .andExpect(jsonPath("$.spells[0].targets[0].Ally").doesNotExist())
                .andExpect(jsonPath("$.spells[0].targets[0].Enemy", is(true)))
                .andExpect(jsonPath("$.spells[0].targets[0].Neutral").doesNotExist())
                .andExpect(jsonPath("$.spells[0].targets[0].Unit", is(true)))
                .andExpect(jsonPath("$.spells[0].targets[0].Tile").doesNotExist())
                .andExpect(jsonPath("$.spells[0].targets[0].Structure").doesNotExist())
                .andExpect(jsonPath("$.spells[0].targets[0].path", is(false)))
                .andExpect(jsonPath("$.spells[1].id", is(1)))
                .andExpect(jsonPath("$.spells[1].name", is("QuickFix")))
                .andExpect(jsonPath("$.spells[1].tags", contains("Defensive")))
                .andExpect(jsonPath("$.spells[1].focusCost", is(2400)))
                .andExpect(jsonPath("$.spells[1].signatureSpell", is(true)))
                .andExpect(jsonPath("$.spells[1].targets", hasSize(1)))
                .andExpect(jsonPath("$.spells[1].targets[0].Own").doesNotExist())
                .andExpect(jsonPath("$.spells[1].targets[0].Ally").doesNotExist())
                .andExpect(jsonPath("$.spells[1].targets[0].Enemy").doesNotExist())
                .andExpect(jsonPath("$.spells[1].targets[0].Neutral", is(true)))
                .andExpect(jsonPath("$.spells[1].targets[0].Unit").doesNotExist())
                .andExpect(jsonPath("$.spells[1].targets[0].Tile", is(true)))
                .andExpect(jsonPath("$.spells[1].targets[0].Structure").doesNotExist())
                .andExpect(jsonPath("$.spells[1].targets[0].path", is(false)))
                .andExpect(jsonPath("$.spells[2].id", is(2)))
                .andExpect(jsonPath("$.spells[2].name", is("HammerForce")))
                .andExpect(jsonPath("$.spells[2].tags", contains("Offensive")))
                .andExpect(jsonPath("$.spells[2].signatureSpell", is(false)))
                .andExpect(jsonPath("$.spells[3].id", is(3)))
                .andExpect(jsonPath("$.spells[3].name", is("ScopeIn")))
                .andExpect(jsonPath("$.spells[3].tags", contains("Offensive")))
                .andExpect(jsonPath("$.spells[3].focusCost", is(1600)))
                .andExpect(jsonPath("$.spells[3].targets", empty()))
                .andExpect(jsonPath("$.spells[3].signatureSpell", is(false)))
                .andExpect(jsonPath("$.spells[4].name", is("Stalwart")))
                .andExpect(jsonPath("$.spells[4].tags", contains("Defensive")))
                .andExpect(jsonPath("$.spells[5].name", is("HunkerDown")))
                .andExpect(jsonPath("$.spells[5].tags", contains("Defensive")))
                .andExpect(jsonPath("$.spells[6].name", is("Tailwind")))
                .andExpect(jsonPath("$.spells[6].tags", contains("Utility")))
                .andExpect(jsonPath("$.spells[7].name", is("Farsight")))
                .andExpect(jsonPath("$.spells[7].tags", contains("Utility")))
                .andExpect(jsonPath("$.spells[7].signatureSpell", is(false)))
                .andExpect(jsonPath("$.structureTypes", hasSize(6)))
                .andExpect(jsonPath("$.structureTypes[0].name", is("Headquarters")))
                .andExpect(jsonPath("$.structureTypes[0].tags", contains("Building")))
                .andExpect(jsonPath("$.structureTypes[0].supplyCost", is(1000)))
                .andExpect(jsonPath("$.structureTypes[0].aetherCost", is(0)))
                .andExpect(jsonPath("$.structureTypes[0].buildTime", is(10000)))
                .andExpect(jsonPath("$.structureTypes[0].cover", is(0.4)))
                .andExpect(jsonPath("$.structureTypes[0].vision", is(2)))
                .andExpect(jsonPath("$.structureTypes[0].actions", empty()))
                .andExpect(jsonPath("$.structureTypes[0].movementTable", is(Map.of(
                        "Air", 1.0, "Land", 1.0
                ))))
                .andExpect(jsonPath("$.structureTypes[1].name", is("Depot")))
                .andExpect(jsonPath("$.structureTypes[1].tags", contains("Building")))
                .andExpect(jsonPath("$.structureTypes[1].supplyCost", is(100)))
                .andExpect(jsonPath("$.structureTypes[1].aetherCost", is(0)))
                .andExpect(jsonPath("$.structureTypes[1].buildTime", is(200)))
                .andExpect(jsonPath("$.structureTypes[1].cover", is(0.3)))
                .andExpect(jsonPath("$.structureTypes[1].vision", is(0)))
                .andExpect(jsonPath("$.structureTypes[1].actions", empty()))
                .andExpect(jsonPath("$.structureTypes[1].movementTable", is(Map.of(
                        "Air", 1.0, "Land", 1.0
                ))))
                .andExpect(jsonPath("$.structureTypes[2].name", is("Tower")))
                .andExpect(jsonPath("$.structureTypes[2].tags", contains("Building")))
                .andExpect(jsonPath("$.structureTypes[2].supplyCost", is(200)))
                .andExpect(jsonPath("$.structureTypes[2].aetherCost", is(0)))
                .andExpect(jsonPath("$.structureTypes[2].buildTime", is(200)))
                .andExpect(jsonPath("$.structureTypes[2].cover", is(0.3)))
                .andExpect(jsonPath("$.structureTypes[2].vision", is(0)))
                .andExpect(jsonPath("$.structureTypes[2].actions", empty()))
                .andExpect(jsonPath("$.structureTypes[2].movementTable", is(Map.of(
                        "Air", 1.0, "Land", 1.0
                ))))
                .andExpect(jsonPath("$.structureTypes[3].name", is("Fort")))
                .andExpect(jsonPath("$.structureTypes[3].tags", contains("Building")))
                .andExpect(jsonPath("$.structureTypes[3].supplyCost", is(300)))
                .andExpect(jsonPath("$.structureTypes[3].aetherCost", is(0)))
                .andExpect(jsonPath("$.structureTypes[3].buildTime", is(200)))
                .andExpect(jsonPath("$.structureTypes[3].cover", is(0.3)))
                .andExpect(jsonPath("$.structureTypes[3].vision", is(0)))
                .andExpect(jsonPath("$.structureTypes[3].actions", contains(
                        "Engineer",
                        "RifleSquad",
                        "RepeaterSquad",
                        "HavocSquad",
                        "Vanguard",
                        "Scorpion",
                        "FieldGun",
                        "APC",
                        "RepairTruck",
                        "Devastator",
                        "Colossus"
                )))
                .andExpect(jsonPath("$.structureTypes[3].movementTable", is(Map.of(
                        "Air", 1.0, "Land", 1.0
                ))))
                .andExpect(jsonPath("$.structureTypes[4].name", is("Port")))
                .andExpect(jsonPath("$.structureTypes[4].tags", contains("Building")))
                .andExpect(jsonPath("$.structureTypes[4].supplyCost", is(300)))
                .andExpect(jsonPath("$.structureTypes[4].aetherCost", is(0)))
                .andExpect(jsonPath("$.structureTypes[4].buildTime", is(200)))
                .andExpect(jsonPath("$.structureTypes[4].cover", is(0.3)))
                .andExpect(jsonPath("$.structureTypes[4].vision", is(0)))
                .andExpect(jsonPath("$.structureTypes[4].actions", contains(
                        "Skiff", "Gunboat", "Corvette", "Aegis", "Dreadnought"
                )))
                .andExpect(jsonPath("$.structureTypes[4].movementTable", is(Map.of(
                        "Air", 1.0, "Land", 1.0, "Naval", 1.0
                ))))
                .andExpect(jsonPath("$.structureTypes[5].name", is("Airfield")))
                .andExpect(jsonPath("$.structureTypes[5].tags", contains("Building")))
                .andExpect(jsonPath("$.structureTypes[5].supplyCost", is(300)))
                .andExpect(jsonPath("$.structureTypes[5].aetherCost", is(200)))
                .andExpect(jsonPath("$.structureTypes[5].buildTime", is(200)))
                .andExpect(jsonPath("$.structureTypes[5].cover", is(0.3)))
                .andExpect(jsonPath("$.structureTypes[5].vision", is(0)))
                .andExpect(jsonPath("$.structureTypes[5].actions", contains(
                        "Ornithopter", "Biplane", "Airship"
                )))
                .andExpect(jsonPath("$.structureTypes[5].movementTable", is(Map.of(
                        "Air", 1.0, "Land", 1.0
                ))))
                .andExpect(jsonPath("$.tileTypes", hasSize(17)))
                .andExpect(jsonPath("$.tileTypes[0].name", is("Plains")))
                .andExpect(jsonPath("$.tileTypes[0].tags", empty()))
                .andExpect(jsonPath("$.tileTypes[0].cover", is(0.1)))
                .andExpect(jsonPath("$.tileTypes[0].movementTable", is(Map.of(
                        "Treaded", 1.0, "Wheeled", 1.5, "Soldier", 1.0, "Air", 1.0
                ))))
                .andExpect(jsonPath("$.tileTypes[1].name", is("Road")))
                .andExpect(jsonPath("$.tileTypes[2].name", is("Bridge")))
                .andExpect(jsonPath("$.tileTypes[3].name", is("River")))
                .andExpect(jsonPath("$.tileTypes[4].name", is("Ruins")))
                .andExpect(jsonPath("$.tileTypes[4].tags", contains("HidingSpot")))
                .andExpect(jsonPath("$.tileTypes[5].name", is("Forest")))
                .andExpect(jsonPath("$.tileTypes[6].name", is("Hill")))
                .andExpect(jsonPath("$.tileTypes[7].name", is("Swamp")))
                .andExpect(jsonPath("$.tileTypes[8].name", is("Mountain")))
                .andExpect(jsonPath("$.tileTypes[9].name", is("Ocean")))
                .andExpect(jsonPath("$.tileTypes[9].tags", contains("DeepWater")))
                .andExpect(jsonPath("$.tileTypes[10].name", is("Beach")))
                .andExpect(jsonPath("$.tileTypes[11].name", is("SeaFog")))
                .andExpect(jsonPath("$.tileTypes[11].tags", containsInAnyOrder("DeepWater", "HidingSpot")))
                .andExpect(jsonPath("$.tileTypes[12].name", is("EarthLeyline")))
                .andExpect(jsonPath("$.tileTypes[12].tags", contains("Leyline")))
                .andExpect(jsonPath("$.tileTypes[13].name", is("FireLeyline")))
                .andExpect(jsonPath("$.tileTypes[13].tags", contains("Leyline")))
                .andExpect(jsonPath("$.tileTypes[14].name", is("WaterLeyline")))
                .andExpect(jsonPath("$.tileTypes[14].tags", contains("Leyline")))
                .andExpect(jsonPath("$.tileTypes[15].name", is("AirLeyline")))
                .andExpect(jsonPath("$.tileTypes[15].tags", contains("Leyline")))
                .andExpect(jsonPath("$.tileTypes[16].name", is("AetherLeyline")))
                .andExpect(jsonPath("$.tileTypes[16].tags", contains("Leyline")))
                .andExpect(jsonPath("$.unitTypes", hasSize(19)))
                .andExpect(jsonPath("$.unitTypes[0].id", is(0)))
                .andExpect(jsonPath("$.unitTypes[0].name", is("Engineer")))
                .andExpect(jsonPath("$.unitTypes[0].tags", containsInAnyOrder("Land", "Soldier")))
                .andExpect(jsonPath("$.unitTypes[0].speed", is(4)))
                .andExpect(jsonPath("$.unitTypes[0].vision", is(2)))
                .andExpect(jsonPath("$.unitTypes[0].supplyCost", is(100)))
                .andExpect(jsonPath("$.unitTypes[0].aetherCost", is(0)))
                .andExpect(jsonPath("$.unitTypes[0].weapons", hasSize(1)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].name", is("Rifle")))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].damage.Engineer", is(40)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].damage.RifleSquad", is(20)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets", hasSize(1)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets[0].Own").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets[0].Ally").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets[0].Enemy", is(true)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets[0].Neutral", is(true)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets[0].Tile").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets[0].Unit", is(true)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets[0].minRange", is(0)))
                .andExpect(jsonPath("$.unitTypes[0].weapons[0].targets[0].maxRange", is(1)))
                .andExpect(jsonPath("$.unitTypes[0].actions", contains(
                        "RepairStructure", "Depot", "Tower", "Fort", "Port", "Airfield"
                )))
                .andExpect(jsonPath("$.unitTypes[1].name", is("RifleSquad")))
                .andExpect(jsonPath("$.unitTypes[2].name", is("RepeaterSquad")))
                .andExpect(jsonPath("$.unitTypes[3].name", is("HavocSquad")))
                .andExpect(jsonPath("$.unitTypes[4].name", is("Vanguard")))
                .andExpect(jsonPath("$.unitTypes[5].name", is("Scorpion")))
                .andExpect(jsonPath("$.unitTypes[6].name", is("FieldGun")))
                .andExpect(jsonPath("$.unitTypes[7].name", is("APC")))
                .andExpect(jsonPath("$.unitTypes[8].name", is("RepairTruck")))
                .andExpect(jsonPath("$.unitTypes[9].name", is("Devastator")))
                .andExpect(jsonPath("$.unitTypes[10].name", is("Colossus")))
                .andExpect(jsonPath("$.unitTypes[11].name", is("Ornithopter")))
                .andExpect(jsonPath("$.unitTypes[12].name", is("Biplane")))
                .andExpect(jsonPath("$.unitTypes[13].name", is("Airship")))
                .andExpect(jsonPath("$.unitTypes[14].name", is("Skiff")))
                .andExpect(jsonPath("$.unitTypes[15].name", is("Gunboat")))
                .andExpect(jsonPath("$.unitTypes[16].name", is("Corvette")))
                .andExpect(jsonPath("$.unitTypes[16].id", is(16)))
                .andExpect(jsonPath("$.unitTypes[16].tags", containsInAnyOrder("Naval", "Ship")))
                .andExpect(jsonPath("$.unitTypes[16].speed", is(6)))
                .andExpect(jsonPath("$.unitTypes[16].vision", is(2)))
                .andExpect(jsonPath("$.unitTypes[16].supplyCost", is(600)))
                .andExpect(jsonPath("$.unitTypes[16].aetherCost", is(60)))
                .andExpect(jsonPath("$.unitTypes[16].weapons", hasSize(2)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].name", is("Cannon")))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets", hasSize(1)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].Own").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].Ally").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].Enemy", is(true)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].Neutral", is(true)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].Structure", is(true)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].Tile").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].Unit", is(true)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].minRange", is(0)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].targets[0].maxRange", is(2)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].damage.Vanguard", is(60)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[0].damage.Building", is(20)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].name", is("MachineGun")))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets", hasSize(1)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets[0].Own").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets[0].Ally").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets[0].Enemy", is(true)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets[0].Neutral", is(true)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets[0].Tile").doesNotExist())
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets[0].Unit", is(true)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets[0].minRange", is(0)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].targets[0].maxRange", is(1)))
                .andExpect(jsonPath("$.unitTypes[16].weapons[1].damage", is(Map.of(
                        "Ornithopter", 60, "Biplane", 50, "Airship", 40
                ))))
                .andExpect(jsonPath("$.unitTypes[16].actions", empty()))
                .andExpect(jsonPath("$.unitTypes[17].name", is("Aegis")))
                .andExpect(jsonPath("$.unitTypes[18].name", is("Dreadnought")));
    }

}
