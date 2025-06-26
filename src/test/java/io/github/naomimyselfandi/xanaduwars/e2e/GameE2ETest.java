package io.github.naomimyselfandi.xanaduwars.e2e;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.intellij.lang.annotations.Language;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "xanadu.core.ruleset.root=testRuleset")
class GameE2ETest extends BaseE2ETest {

    // For now, we haven't fully implemented maps, so this uses the
    // developer-only ad-hoc game endpoint. This will change once maps are set
    // up properly.

    private String id;

    private static final @Language("json") String MAP_CONTENT = """
            {
              "players": [
                {"team": 0},
                {"team": 1}
              ],
              "tiles": [
                [
                  {"type": 0, "structure": {"type": 0, "owner": 0}},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0}
                ],
                [
                  {"type": 0, "unit": {"type": 1, "owner": 0}},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0, "unit": {"type": 1, "owner": 1}}
                ],
                [
                  {"type": 0},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0},
                  {"type": 0, "structure": {"type": 0, "owner": 1}}
                ]
              ]
            }
            """;

    @E2ETest(roles = Role.DEVELOPER)
    void canCreateAndLeaveGame() throws Exception {
        createGame();
        query("/game/{id}/drop", id)
                .post()
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        query("/game/{id}", id)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELED")))
                .andExpect(jsonPath("$.playerSlots", hasSize(0)));
    }

    @E2ETest(roles = Role.DEVELOPER)
    void canJoinAndLeaveGame() throws Exception {
        createGame();
        var host = account;
        login(createAccount());
        query("/game/{id}/join", id)
                .post()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.host", is(host.getId().toString())))
                .andExpect(jsonPath("$.turnCount", is(1)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.playerSlots", hasSize(2)))
                .andExpect(jsonPath("$.playerSlots[0].id", is(0)))
                .andExpect(jsonPath("$.playerSlots[0].account.id", is(host.getId().toString())))
                .andExpect(jsonPath("$.playerSlots[0].account.username", is(host.getUsername().toString())))
                .andExpect(jsonPath("$.playerSlots[1].id", is(1)))
                .andExpect(jsonPath("$.playerSlots[1].account.id", is(account.getId().toString())))
                .andExpect(jsonPath("$.playerSlots[1].account.username", is(account.getUsername().toString())));
        query("/game/{id}/drop", id)
                .post()
                .andExpect(status().isNoContent());
        login(host);
        query("/game/{id}", id)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.host", is(host.getId().toString())))
                .andExpect(jsonPath("$.turnCount", is(1)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.playerSlots", hasSize(1)))
                .andExpect(jsonPath("$.playerSlots[0].id", is(0)))
                .andExpect(jsonPath("$.playerSlots[0].account.id", is(host.getId().toString())))
                .andExpect(jsonPath("$.playerSlots[0].account.username", is(host.getUsername().toString())));
    }

    @E2ETest(roles = Role.DEVELOPER)
    void canSetPregameChoices() throws Exception {
        createGame();
        query("/game/{id}/choices", id)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("spells", hasSize(0)));
        query("/game/{id}/choices", id)
                .withContent("""
                    {
                      "commander": 0,
                      "spells": [2, 6]
                    }
                    """)
                .put()
                .andExpect(status().isOk())
                .andExpect(jsonPath("commander", is(0)))
                .andExpect(jsonPath("spells", contains(2, 6)));
        query("/game/{id}/choices", id)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("commander", is(0)))
                .andExpect(jsonPath("spells", contains(2, 6)));
    }

    @E2ETest(roles = Role.DEVELOPER)
    void pregameChoicesAreValidated() throws Exception {
        createGame();
        query("/game/{id}/choices", id)
                .withContent("""
                    {
                      "commander": 0,
                      "spells": [2, 6]
                    }
                    """)
                .put()
                .andExpect(status().isOk());
        query("/game/{id}/choices", id)
                .withContent("""
                    {
                      "commander": 0,
                      "spells": [4, 6]
                    }
                    """)
                .put()
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.details", is("Invalid combination of spells for Alice.")));
        query("/game/{id}/choices", id)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("commander", is(0)))
                .andExpect(jsonPath("spells", contains(2, 6)));
    }

    @E2ETest(roles = Role.DEVELOPER)
    void canStartAndViewGame() throws Exception {
        createGame();
        var host = account;
        query("/game/{id}/choices", id)
                .withContent("""
                    {
                      "commander": 0,
                      "spells": [2, 6]
                    }
                    """)
                .put()
                .andExpect(status().isOk());
        var opponent = login(createAccount());
        query("/game/{id}/join", id)
                .post()
                .andExpect(status().isOk());
        query("/game/{id}/choices", id)
                .withContent("""
                    {
                      "commander": 1,
                      "spells": [4, 5]
                    }
                    """)
                .put()
                .andExpect(status().isOk());
        login(host);
        query("/game/{id}/start", id)
                .post()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ONGOING")));
        query("/game/{id}/state", id)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("1.2.3")))
                .andExpect(jsonPath("$.activePlayer", is(0)))
                .andExpect(jsonPath("$.turn", is(0)))
                .andExpect(jsonPath("$.players[0].id", is(0)))
                .andExpect(jsonPath("$.players[0].commander", is(0)))
                .andExpect(jsonPath("$.players[0].team", is(0)))
                .andExpect(jsonPath("$.players[0].spellSlots", hasSize(3)))
                .andExpect(jsonPath("$.players[0].spellSlots[0].spell", is(0)))
                .andExpect(jsonPath("$.players[0].spellSlots[0].active", is(false)))
                .andExpect(jsonPath("$.players[0].spellSlots[0].revealed", is(true)))
                .andExpect(jsonPath("$.players[0].spellSlots[1].spell", is(2)))
                .andExpect(jsonPath("$.players[0].spellSlots[1].active", is(false)))
                .andExpect(jsonPath("$.players[0].spellSlots[1].revealed", is(false)))
                .andExpect(jsonPath("$.players[0].spellSlots[2].spell", is(6)))
                .andExpect(jsonPath("$.players[0].spellSlots[2].active", is(false)))
                .andExpect(jsonPath("$.players[0].spellSlots[2].revealed", is(false)))
                .andExpect(jsonPath("$.players[0].supplies", is(100)))
                .andExpect(jsonPath("$.players[0].aether", is(0)))
                .andExpect(jsonPath("$.players[0].focus", is(0)))
                .andExpect(jsonPath("$.players[0].active", is(true)))
                .andExpect(jsonPath("$.players[0].defeated", is(false)))
                .andExpect(jsonPath("$.players[1].id", is(1)))
                .andExpect(jsonPath("$.players[1].commander", is(1)))
                .andExpect(jsonPath("$.players[1].team", is(1)))
                .andExpect(jsonPath("$.players[1].spellSlots", hasSize(1)))
                .andExpect(jsonPath("$.players[1].spellSlots[0].spell", is(1)))
                .andExpect(jsonPath("$.players[1].spellSlots[0].active", is(false)))
                .andExpect(jsonPath("$.players[1].spellSlots[0].revealed", is(true)))
                .andExpect(jsonPath("$.players[1].supplies", is(0)))
                .andExpect(jsonPath("$.players[1].aether", is(0)))
                .andExpect(jsonPath("$.players[1].focus", is(0)))
                .andExpect(jsonPath("$.players[1].active", is(false)))
                .andExpect(jsonPath("$.players[1].defeated", is(false)))
                .andExpect(jsonPath("$.tiles[0][0].id.x", is(0)))
                .andExpect(jsonPath("$.tiles[0][0].id.y", is(0)))
                .andExpect(jsonPath("$.tiles[0][0].type", is(0)))
                .andExpect(jsonPath("$.tiles[0][1].id.x", is(1)))
                .andExpect(jsonPath("$.tiles[0][1].id.y", is(0)))
                .andExpect(jsonPath("$.tiles[0][1].type", is(0)))
                .andExpect(jsonPath("$.tiles[1][0].id.x", is(0)))
                .andExpect(jsonPath("$.tiles[1][0].id.y", is(1)))
                .andExpect(jsonPath("$.tiles[1][0].type", is(0)))
                .andExpect(jsonPath("$.tiles[1][0].unit.type", is(1)))
                .andExpect(jsonPath("$.tiles[1][5].unit").doesNotExist())
                .andExpect(jsonPath("$.tiles[0][0].structure.type", is(0)))
                .andExpect(jsonPath("$.tiles[0][0].structure.owner", is(0)))
                .andExpect(jsonPath("$.tiles[2][5].structure.type", is(0)))
                .andExpect(jsonPath("$.tiles[2][5].structure.owner").doesNotExist());
        login(opponent);
        query("/game/{id}/state", id)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("1.2.3")))
                .andExpect(jsonPath("$.activePlayer", is(0)))
                .andExpect(jsonPath("$.turn", is(0)))
                .andExpect(jsonPath("$.players[0].id", is(0)))
                .andExpect(jsonPath("$.players[0].commander", is(0)))
                .andExpect(jsonPath("$.players[0].team", is(0)))
                .andExpect(jsonPath("$.players[0].spellSlots", hasSize(1)))
                .andExpect(jsonPath("$.players[0].spellSlots[0].spell", is(0)))
                .andExpect(jsonPath("$.players[0].spellSlots[0].active", is(false)))
                .andExpect(jsonPath("$.players[0].spellSlots[0].revealed", is(true)))
                .andExpect(jsonPath("$.players[0].supplies", is(0)))
                .andExpect(jsonPath("$.players[0].aether", is(0)))
                .andExpect(jsonPath("$.players[0].focus", is(0)))
                .andExpect(jsonPath("$.players[0].active", is(true)))
                .andExpect(jsonPath("$.players[0].defeated", is(false)))
                .andExpect(jsonPath("$.players[1].id", is(1)))
                .andExpect(jsonPath("$.players[1].commander", is(1)))
                .andExpect(jsonPath("$.players[1].team", is(1)))
                .andExpect(jsonPath("$.players[1].spellSlots", hasSize(3)))
                .andExpect(jsonPath("$.players[1].spellSlots[0].spell", is(1)))
                .andExpect(jsonPath("$.players[1].spellSlots[0].active", is(false)))
                .andExpect(jsonPath("$.players[1].spellSlots[0].revealed", is(true)))
                .andExpect(jsonPath("$.players[1].spellSlots[1].spell", is(4)))
                .andExpect(jsonPath("$.players[1].spellSlots[1].active", is(false)))
                .andExpect(jsonPath("$.players[1].spellSlots[1].revealed", is(false)))
                .andExpect(jsonPath("$.players[1].spellSlots[2].spell", is(5)))
                .andExpect(jsonPath("$.players[1].spellSlots[2].active", is(false)))
                .andExpect(jsonPath("$.players[1].spellSlots[2].revealed", is(false)))
                .andExpect(jsonPath("$.players[1].supplies", is(0)))
                .andExpect(jsonPath("$.players[1].aether", is(0)))
                .andExpect(jsonPath("$.players[1].focus", is(0)))
                .andExpect(jsonPath("$.players[1].active", is(false)))
                .andExpect(jsonPath("$.players[1].defeated", is(false)))
                .andExpect(jsonPath("$.tiles[0][0].id.x", is(0)))
                .andExpect(jsonPath("$.tiles[0][0].id.y", is(0)))
                .andExpect(jsonPath("$.tiles[0][0].type", is(0)))
                .andExpect(jsonPath("$.tiles[0][1].id.x", is(1)))
                .andExpect(jsonPath("$.tiles[0][1].id.y", is(0)))
                .andExpect(jsonPath("$.tiles[0][1].type", is(0)))
                .andExpect(jsonPath("$.tiles[1][0].id.x", is(0)))
                .andExpect(jsonPath("$.tiles[1][0].id.y", is(1)))
                .andExpect(jsonPath("$.tiles[1][0].type", is(0)))
                .andExpect(jsonPath("$.tiles[1][0].unit").doesNotExist())
                .andExpect(jsonPath("$.tiles[1][5].unit.type", is(1)))
                .andExpect(jsonPath("$.tiles[0][0].structure.type", is(0)))
                .andExpect(jsonPath("$.tiles[0][0].structure.owner").doesNotExist())
                .andExpect(jsonPath("$.tiles[2][5].structure.type", is(0)))
                .andExpect(jsonPath("$.tiles[2][5].structure.owner", is(1)));
    }

    private void createGame() throws Exception {
        @JsonIgnoreProperties(ignoreUnknown = true)
        record IdCaptor(String id) {}
        id = query("/game/withAdHocMap")
                .withContent(MAP_CONTENT)
                .extractingBody()
                .as(IdCaptor.class)
                .post()
                .id;
    }

}
