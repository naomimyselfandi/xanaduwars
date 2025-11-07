package io.github.naomimyselfandi.xanaduwars.e2e;

import com.jayway.jsonpath.JsonPath;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.map.entity.GameMap;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GameMapControllerE2ETest extends AbstractE2ETest {

    private static final List<String> NAMES = new ArrayList<>();

    @AutoUser
    private TestUser author, anotherUser;

    @AutoUser(Role.JUDGE)
    private TestUser judge;

    private String name, tile1, tile2, unit;

    @BeforeEach
    void setup(SeededRng random) {
        name = random.not(NAMES.toArray(String[]::new));
        NAMES.add(name);
        tile1 = random.nextString();
        tile2 = random.nextString();
        unit = random.nextString();
    }

    @Test
    void update() throws Exception {
        var id = createMap(author);
        as(author)
                .perform(patch("/map/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "%s",
                          "width": 2,
                          "height": 2,
                          "tiles": [
                            {"type": "%s"},
                            {"type": "%s", "unit": {"type": "%s", "owner": 1}},
                            {"type": "%s"},
                            {"type": "%s"}
                          ],
                          "playerSlots": [
                            {"team": 0},
                            {"team": 1},
                            {"team": 2}
                          ]
                        }
                        """.formatted(name, tile1, tile2, unit, tile1, tile2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(id)))
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("status", is("UNPUBLISHED")))
                .andExpect(jsonPath("author.id", is(author.id().toString())))
                .andExpect(jsonPath("author.username", is(author.username().toString())))
                .andExpect(jsonPath("author._links.self.href", endsWith("/account/" + author.id())))
                .andExpect(jsonPath("width", is(2)))
                .andExpect(jsonPath("height", is(2)))
                .andExpect(jsonPath("tiles", hasSize(4)))
                .andExpect(jsonPath("tiles[0].type", is(tile1)))
                .andExpect(jsonPath("tiles[0].unit").doesNotExist())
                .andExpect(jsonPath("tiles[1].type", is(tile2)))
                .andExpect(jsonPath("tiles[1].unit.type", is(unit)))
                .andExpect(jsonPath("tiles[1].unit.owner", is(1)))
                .andExpect(jsonPath("tiles[2].type", is(tile1)))
                .andExpect(jsonPath("tiles[2].unit").doesNotExist())
                .andExpect(jsonPath("tiles[3].type", is(tile2)))
                .andExpect(jsonPath("tiles[3].unit").doesNotExist())
                .andExpect(jsonPath("playerSlots", hasSize(3)))
                .andExpect(jsonPath("playerSlots[0].team", is(0)))
                .andExpect(jsonPath("playerSlots[1].team", is(1)))
                .andExpect(jsonPath("playerSlots[2].team", is(2)));
        as(author)
                .perform(get("/map/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(id)))
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("status", is("UNPUBLISHED")))
                .andExpect(jsonPath("author.id", is(author.id().toString())))
                .andExpect(jsonPath("author.username", is(author.username().toString())))
                .andExpect(jsonPath("author._links.self.href", endsWith("/account/" + author.id())))
                .andExpect(jsonPath("width", is(2)))
                .andExpect(jsonPath("height", is(2)))
                .andExpect(jsonPath("tiles", hasSize(4)))
                .andExpect(jsonPath("tiles[0].type", is(tile1)))
                .andExpect(jsonPath("tiles[0].unit").doesNotExist())
                .andExpect(jsonPath("tiles[1].type", is(tile2)))
                .andExpect(jsonPath("tiles[1].unit.type", is(unit)))
                .andExpect(jsonPath("tiles[1].unit.owner", is(1)))
                .andExpect(jsonPath("tiles[2].type", is(tile1)))
                .andExpect(jsonPath("tiles[2].unit").doesNotExist())
                .andExpect(jsonPath("tiles[3].type", is(tile2)))
                .andExpect(jsonPath("tiles[3].unit").doesNotExist())
                .andExpect(jsonPath("playerSlots", hasSize(3)))
                .andExpect(jsonPath("playerSlots[0].team", is(0)))
                .andExpect(jsonPath("playerSlots[1].team", is(1)))
                .andExpect(jsonPath("playerSlots[2].team", is(2)));
    }

    @Test
    void update_OtherUsersCannotModifyMaps() throws Exception {
        var id = createMap(author);
        as(author).perform(patch("/map/{id}/status", id).queryParam("status", "PUBLISHED"));
        as(anotherUser)
                .perform(patch("/map/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "%s",
                          "width": 2,
                          "height": 2,
                          "tiles": [
                            {"type": "%s"},
                            {"type": "%s", "unit": {"type": "%s", "owner": 1}},
                            {"type": "%s"},
                            {"type": "%s"}
                          ],
                          "playerSlots": [
                            {"team": 0},
                            {"team": 1},
                            {"team": 2}
                          ]
                        }
                        """.formatted(name, tile1, tile2, unit, tile1, tile2)))
                .andExpect(status().isForbidden());
        as(author)
                .perform(get("/map/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(id)))
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("status", is("PUBLISHED")))
                .andExpect(jsonPath("author.id", is(author.id().toString())))
                .andExpect(jsonPath("author.username", is(author.username().toString())))
                .andExpect(jsonPath("author._links.self.href", endsWith("/account/" + author.id())))
                .andExpect(jsonPath("width", is(2)))
                .andExpect(jsonPath("height", is(2)))
                .andExpect(jsonPath("tiles", hasSize(4)))
                .andExpect(jsonPath("tiles[0].type", is(tile1)))
                .andExpect(jsonPath("tiles[0].unit").doesNotExist())
                .andExpect(jsonPath("tiles[1].type", is(tile2)))
                .andExpect(jsonPath("tiles[1].unit").doesNotExist())
                .andExpect(jsonPath("tiles[2].type", is(tile1)))
                .andExpect(jsonPath("tiles[2].unit").doesNotExist())
                .andExpect(jsonPath("tiles[3].type", is(tile2)))
                .andExpect(jsonPath("tiles[3].unit.type", is(unit)))
                .andExpect(jsonPath("tiles[3].unit.owner", is(1)))
                .andExpect(jsonPath("playerSlots", hasSize(2)))
                .andExpect(jsonPath("playerSlots[0].team", is(0)))
                .andExpect(jsonPath("playerSlots[1].team", is(1)));
    }

    @MethodSource
    @ParameterizedTest
    void updateStatus(GameMap.Status status1, GameMap.Status status2) throws Exception {
        var id = createMap(author);
        var s1 = status1.name();
        var s2 = status2.name();
        as(author)
                .perform(patch("/map/{id}/status", id).queryParam("status", s1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(s1)));
        as(author)
                .perform(get("/map/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(s1)));
        as(author)
                .perform(patch("/map/{id}/status", id).queryParam("status", s2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(s2)));
        as(author)
                .perform(get("/map/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(s2)));
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "OFFICIAL")
    void updateStatus_RegularUsersCannotApproveMaps(GameMap.Status initialStatus) throws Exception {
        var id = createMap(author);
        var status = initialStatus.name();
        if (initialStatus != GameMap.Status.UNPUBLISHED) {
            as(author)
                    .perform(patch("/map/{id}/status", id).queryParam("status", status))
                    .andExpect(status().isOk());
        }
        as(author)
                .perform(patch("/map/{id}/status", id).queryParam("status", "OFFICIAL"))
                .andExpect(status().isForbidden());
        as(author)
                .perform(get("/map/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(status)));
    }

    @ParameterizedTest
    @EnumSource(names = {"PUBLISHED", "OFFICIAL"})
    void updateStatus_JudgesCanManageSubmissions(GameMap.Status judgeDecision) throws Exception {
        var id = createMap(author);
        var status = judgeDecision.name();
        as(author)
                .perform(patch("/map/{id}/status", id).queryParam("status", "SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is("SUBMITTED")));
        as(judge)
                .perform(patch("/map/{id}/status", id).queryParam("status", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(status)));
        as(author)
                .perform(get("/map/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(status)));
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "OFFICIAL")
    void updateStatus_JudgesCannotUnpublishMaps(GameMap.Status initialStatus) throws Exception {
        var id = createMap(author);
        var status = initialStatus.name();
        if (initialStatus != GameMap.Status.UNPUBLISHED) {
            as(author)
                    .perform(patch("/map/{id}/status", id).queryParam("status", status))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("status", is(status)));
        }
        as(judge)
                .perform(patch("/map/{id}/status", id).queryParam("status", "UNPUBLISHED"))
                .andExpect(status().isForbidden());
        as(author)
                .perform(get("/map/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status", is(status)));
    }

    @Test
    void getMap() throws Exception {
        var id = createMap(author);
        as(author).perform(get("/map/{id}", (Object) id)).andExpect(status().isOk())
                .andExpect(jsonPath("id").isString())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("status", is("UNPUBLISHED")))
                .andExpect(jsonPath("author.id", is(author.id().toString())))
                .andExpect(jsonPath("author.username", is(author.username().toString())))
                .andExpect(jsonPath("author._links.self.href", endsWith("/account/" + author.id())))
                .andExpect(jsonPath("width", is(2)))
                .andExpect(jsonPath("height", is(2)))
                .andExpect(jsonPath("tiles", hasSize(4)))
                .andExpect(jsonPath("tiles[0].type", is(tile1)))
                .andExpect(jsonPath("tiles[0].unit").doesNotExist())
                .andExpect(jsonPath("tiles[1].type", is(tile2)))
                .andExpect(jsonPath("tiles[1].unit").doesNotExist())
                .andExpect(jsonPath("tiles[2].type", is(tile1)))
                .andExpect(jsonPath("tiles[2].unit").doesNotExist())
                .andExpect(jsonPath("tiles[3].type", is(tile2)))
                .andExpect(jsonPath("tiles[3].unit.type", is(unit)))
                .andExpect(jsonPath("tiles[3].unit.owner", is(1)))
                .andExpect(jsonPath("playerSlots", hasSize(2)))
                .andExpect(jsonPath("playerSlots[0].team", is(0)))
                .andExpect(jsonPath("playerSlots[1].team", is(1)));
        as(anotherUser)
                .perform(get("/map/{id}", id))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("name").doesNotExist())
                .andExpect(jsonPath("author").doesNotExist());
    }

    @ParameterizedTest
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"UNPUBLISHED"})
    void get_AnyUserCanGetAPublishedMap(GameMap.Status status) throws Exception {
        var id = createMap(author);
        if (status == GameMap.Status.OFFICIAL) {
            as(author)
                    .perform(patch("/map/{id}/status", id).queryParam("status", "SUBMITTED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("status", is("SUBMITTED")));
            as(judge)
                    .perform(patch("/map/{id}/status", id).queryParam("status", "OFFICIAL"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("status", is("OFFICIAL")));
        } else {
            as(author)
                    .perform(patch("/map/{id}/status", id).queryParam("status", status.name()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("status", is(status.name())));
        }
        as(author).perform(get("/map/{id}", (Object) id)).andExpect(status().isOk())
                .andExpect(jsonPath("id").isString())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("status", is(status.name())))
                .andExpect(jsonPath("author.id", is(author.id().toString())))
                .andExpect(jsonPath("author.username", is(author.username().toString())))
                .andExpect(jsonPath("author._links.self.href", endsWith("/account/" + author.id())))
                .andExpect(jsonPath("width", is(2)))
                .andExpect(jsonPath("height", is(2)))
                .andExpect(jsonPath("tiles", hasSize(4)))
                .andExpect(jsonPath("tiles[0].type", is(tile1)))
                .andExpect(jsonPath("tiles[0].unit").doesNotExist())
                .andExpect(jsonPath("tiles[1].type", is(tile2)))
                .andExpect(jsonPath("tiles[1].unit").doesNotExist())
                .andExpect(jsonPath("tiles[2].type", is(tile1)))
                .andExpect(jsonPath("tiles[2].unit").doesNotExist())
                .andExpect(jsonPath("tiles[3].type", is(tile2)))
                .andExpect(jsonPath("tiles[3].unit.type", is(unit)))
                .andExpect(jsonPath("tiles[3].unit.owner", is(1)))
                .andExpect(jsonPath("playerSlots", hasSize(2)))
                .andExpect(jsonPath("playerSlots[0].team", is(0)))
                .andExpect(jsonPath("playerSlots[1].team", is(1)));
        as(judge).perform(get("/map/{id}", (Object) id)).andExpect(status().isOk())
                .andExpect(jsonPath("id").isString())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("status", is(status.name())))
                .andExpect(jsonPath("author.id", is(author.id().toString())))
                .andExpect(jsonPath("author.username", is(author.username().toString())))
                .andExpect(jsonPath("author._links.self.href", endsWith("/account/" + author.id())))
                .andExpect(jsonPath("width", is(2)))
                .andExpect(jsonPath("height", is(2)))
                .andExpect(jsonPath("tiles", hasSize(4)))
                .andExpect(jsonPath("tiles[0].type", is(tile1)))
                .andExpect(jsonPath("tiles[0].unit").doesNotExist())
                .andExpect(jsonPath("tiles[1].type", is(tile2)))
                .andExpect(jsonPath("tiles[1].unit").doesNotExist())
                .andExpect(jsonPath("tiles[2].type", is(tile1)))
                .andExpect(jsonPath("tiles[2].unit").doesNotExist())
                .andExpect(jsonPath("tiles[3].type", is(tile2)))
                .andExpect(jsonPath("tiles[3].unit.type", is(unit)))
                .andExpect(jsonPath("tiles[3].unit.owner", is(1)))
                .andExpect(jsonPath("playerSlots", hasSize(2)))
                .andExpect(jsonPath("playerSlots[0].team", is(0)))
                .andExpect(jsonPath("playerSlots[1].team", is(1)));
        as(anotherUser).perform(get("/map/{id}", (Object) id)).andExpect(status().isOk())
                .andExpect(jsonPath("id").isString())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("status", is(status.name())))
                .andExpect(jsonPath("author.id", is(author.id().toString())))
                .andExpect(jsonPath("author.username", is(author.username().toString())))
                .andExpect(jsonPath("author._links.self.href", endsWith("/account/" + author.id())))
                .andExpect(jsonPath("width", is(2)))
                .andExpect(jsonPath("height", is(2)))
                .andExpect(jsonPath("tiles", hasSize(4)))
                .andExpect(jsonPath("tiles[0].type", is(tile1)))
                .andExpect(jsonPath("tiles[0].unit").doesNotExist())
                .andExpect(jsonPath("tiles[1].type", is(tile2)))
                .andExpect(jsonPath("tiles[1].unit").doesNotExist())
                .andExpect(jsonPath("tiles[2].type", is(tile1)))
                .andExpect(jsonPath("tiles[2].unit").doesNotExist())
                .andExpect(jsonPath("tiles[3].type", is(tile2)))
                .andExpect(jsonPath("tiles[3].unit.type", is(unit)))
                .andExpect(jsonPath("tiles[3].unit.owner", is(1)))
                .andExpect(jsonPath("playerSlots", hasSize(2)))
                .andExpect(jsonPath("playerSlots[0].team", is(0)))
                .andExpect(jsonPath("playerSlots[1].team", is(1)));
    }

    private String createMap(TestUser author) throws Exception {
        var response = as(author)
                .perform(post("/map")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "width": 2,
                                  "height": 2,
                                  "tiles": [
                                    {"type": "%s"},
                                    {"type": "%s"},
                                    {"type": "%s"},
                                    {"type": "%s", "unit": {"type": "%s", "owner": 1}}
                                  ],
                                  "playerSlots": [
                                    {"team": 0},
                                    {"team": 1}
                                  ]
                                }
                                """.formatted(name, tile1, tile2, tile1, tile2, unit)));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").isString())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("status", is("UNPUBLISHED")))
                .andExpect(jsonPath("author.id", is(this.author.id().toString())))
                .andExpect(jsonPath("author.username", is(this.author.username().toString())))
                .andExpect(jsonPath("author._links.self.href", endsWith("/account/" + this.author.id())))
                .andExpect(jsonPath("width", is(2)))
                .andExpect(jsonPath("height", is(2)))
                .andExpect(jsonPath("tiles", hasSize(4)))
                .andExpect(jsonPath("tiles[0].type", is(tile1)))
                .andExpect(jsonPath("tiles[0].unit").doesNotExist())
                .andExpect(jsonPath("tiles[1].type", is(tile2)))
                .andExpect(jsonPath("tiles[1].unit").doesNotExist())
                .andExpect(jsonPath("tiles[2].type", is(tile1)))
                .andExpect(jsonPath("tiles[2].unit").doesNotExist())
                .andExpect(jsonPath("tiles[3].type", is(tile2)))
                .andExpect(jsonPath("tiles[3].unit.type", is(unit)))
                .andExpect(jsonPath("tiles[3].unit.owner", is(1)))
                .andExpect(jsonPath("playerSlots", hasSize(2)))
                .andExpect(jsonPath("playerSlots[0].team", is(0)))
                .andExpect(jsonPath("playerSlots[1].team", is(1)));
        return response
                .andReturn()
                .getResponse()
                .getContentAsString()
                .transform(content -> JsonPath.read(content, "$.id"));
    }

    private static Stream<Arguments> updateStatus() {
        var statuses = new ArrayList<>(Arrays.asList(GameMap.Status.values()));
        statuses.remove(GameMap.Status.OFFICIAL);
        return statuses
                .stream()
                .filter(it -> it != GameMap.Status.UNPUBLISHED)
                .flatMap(status1 -> statuses
                        .stream()
                        .filter(it -> it != status1)
                        .map(status2 -> arguments(status1, status2)));
    }

}
