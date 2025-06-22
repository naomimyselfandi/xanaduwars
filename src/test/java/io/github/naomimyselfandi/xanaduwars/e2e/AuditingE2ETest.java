package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuditingE2ETest extends BaseE2ETest {

    @E2ETest(roles = Role.ADMIN)
    void testBasicFunctionality() throws Exception {
        var fakeId = UUID.randomUUID();
        var fakeAction = UUID.randomUUID();
        query("/audit?accountId={id}", fakeId)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.size", is(10)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$.page.totalElements", is(0)))
                .andExpect(jsonPath("$.page.totalPages", is(0)));
        query("/audit?action={action}", fakeAction)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.size", is(10)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$.page.totalElements", is(0)))
                .andExpect(jsonPath("$.page.totalPages", is(0)));
        var query = "action=AUDIT_READ&accountId=%s".formatted(account.getId());
        query("/audit?" + query)
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountId", is(account.getId().id().toString())))
                .andExpect(jsonPath("$.content[0].username", is(account.getUsername().username())))
                .andExpect(jsonPath("$.content[0].httpMethod", is("GET")))
                .andExpect(jsonPath("$.content[0].httpPath", is("/audit")))
                .andExpect(jsonPath("$.content[0].httpQuery", is(query)))
                .andExpect(jsonPath("$.content[0].action", is("AUDIT_READ")))
                .andExpect(jsonPath("$.content[0].sourceClass", is("i.g.n.x.a.s.AuditServiceImpl")))
                .andExpect(jsonPath("$.content[0].sourceMethod", is("find")))
                .andExpect(jsonPath("$.content[1].accountId", is(account.getId().id().toString())))
                .andExpect(jsonPath("$.content[1].username", is(account.getUsername().username())))
                .andExpect(jsonPath("$.content[1].httpMethod", is("GET")))
                .andExpect(jsonPath("$.content[1].httpPath", is("/audit")))
                .andExpect(jsonPath("$.content[1].httpQuery", is("action=%s".formatted(fakeAction))))
                .andExpect(jsonPath("$.content[1].action", is("AUDIT_READ")))
                .andExpect(jsonPath("$.content[1].sourceClass", is("i.g.n.x.a.s.AuditServiceImpl")))
                .andExpect(jsonPath("$.content[1].sourceMethod", is("find")))
                .andExpect(jsonPath("$.content[2].accountId", is(account.getId().id().toString())))
                .andExpect(jsonPath("$.content[2].username", is(account.getUsername().username())))
                .andExpect(jsonPath("$.content[2].httpMethod", is("GET")))
                .andExpect(jsonPath("$.content[2].httpPath", is("/audit")))
                .andExpect(jsonPath("$.content[2].httpQuery", is("accountId=%s".formatted(fakeId))))
                .andExpect(jsonPath("$.content[2].action", is("AUDIT_READ")))
                .andExpect(jsonPath("$.content[2].sourceClass", is("i.g.n.x.a.s.AuditServiceImpl")))
                .andExpect(jsonPath("$.content[2].sourceMethod", is("find")))
                .andExpect(jsonPath("$.page.size", is(10)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$.page.totalElements", is(3)))
                .andExpect(jsonPath("$.page.totalPages", is(1)));
        query("/audit?page=1&size=2")
                .get()
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].httpQuery", is("action=%s".formatted(fakeAction))))
                .andExpect(jsonPath("$.content[1].httpQuery", is("accountId=%s".formatted(fakeId))))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.number", is(1)))
                .andExpect(jsonPath("$.page.totalElements", is(4)))
                .andExpect(jsonPath("$.page.totalPages", is(2)));
    }

    @E2ETest(roles = {Role.SUPPORT, Role.MODERATOR})
    void canFilterByActionNames() throws Exception {
        var fakeId = UUID.randomUUID();
        query("/account/{id}/full", fakeId).get();
        query("/audit?action=ACCOUNT_READ_FULL")
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].accountId", is(account.getId().id().toString())))
                .andExpect(jsonPath("$.content[0].username", is(account.getUsername().username())))
                .andExpect(jsonPath("$.content[0].httpMethod", is("GET")))
                .andExpect(jsonPath("$.content[0].httpPath", is("/account/%s/full".formatted(fakeId))))
                .andExpect(jsonPath("$.content[0].httpQuery", nullValue()))
                .andExpect(jsonPath("$.content[0].action", is("ACCOUNT_READ_FULL")))
                .andExpect(jsonPath("$.content[0].sourceClass", is("i.g.n.x.a.c.AccountController")))
                .andExpect(jsonPath("$.content[0].sourceMethod", is("getFull")))
                .andExpect(jsonPath("$.page.size", is(10)))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$.page.totalElements", is(1)))
                .andExpect(jsonPath("$.page.totalPages", is(1)));
    }

    @E2ETest(roles = {Role.SUPPORT, Role.MODERATOR})
    void canSortByActionName() throws Exception {
        query("/account/{id}/full", UUID.randomUUID()).get();
        query("/audit?sort=action,asc&accountId={accountId}", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].action", is("ACCOUNT_READ_FULL")))
                .andExpect(jsonPath("$.content[1].action", is("AUDIT_READ")));
        query("/audit?sort=action,desc&accountId={accountId}", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].action", is("AUDIT_READ")))
                .andExpect(jsonPath("$.content[1].action", is("AUDIT_READ")))
                .andExpect(jsonPath("$.content[2].action", is("ACCOUNT_READ_FULL")));
    }

    @E2ETest(roles = {Role.SUPPORT, Role.MODERATOR})
    void canSortByTimestamp() throws Exception {
        query("/account/{id}/full", UUID.randomUUID()).get();
        query("/audit?accountId={accountId}", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].action", is("AUDIT_READ")))
                .andExpect(jsonPath("$.content[1].action", is("ACCOUNT_READ_FULL")));
        query("/audit?sort=timestamp,asc&accountId={accountId}", account.getId())
                .get()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].action", is("ACCOUNT_READ_FULL")))
                .andExpect(jsonPath("$.content[1].action", is("AUDIT_READ")))
                .andExpect(jsonPath("$.content[2].action", is("AUDIT_READ")));
    }

}
