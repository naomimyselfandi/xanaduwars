package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.account.value.RoleSet;
import io.github.naomimyselfandi.xanaduwars.security.dto.AuditLogFilterDto;
import io.github.naomimyselfandi.xanaduwars.security.service.AuditService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BypassIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuditService auditService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BypassTestHelper bypassTestHelper;

    @AfterEach
    void teardown() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            MODERATOR,true
            ADMIN,true
            DEVELOPER,false
            """)
    void bypass(Role role, boolean bypass, SeededRng random) {
        var account = accountRepository.save(random.<Account>get().setRoles(RoleSet.of(Set.of(role))));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserDetailsDto(
                        account.getId(),
                        account.getUsername(),
                        account.getEmailAddress(),
                        random.get(),
                        account.getRoles(),
                        account.isRememberMe()
                ),
                null
        ));
        assertThat(bypassTestHelper.testMethod()).isEqualTo(bypass);
        var logs = auditService.find(new AuditLogFilterDto()
                        .setAccountId(account.getId())
                        .setAction("BYPASS_ACCESS_CHECK"),
                Pageable.unpaged());
        if (bypass) {
            assertThat(logs).singleElement().satisfies(it -> {
                assertThat(it.id()).isNotNull();
                var fiveSeconds = new TemporalUnitWithinOffset(5, ChronoUnit.SECONDS);
                assertThat(it.timestamp()).isCloseTo(Instant.now(), fiveSeconds);
                assertThat(it.accountId()).isEqualTo(account.getId());
                assertThat(it.username()).isEqualTo(account.getUsername().toString());
                assertThat(it.httpMethod()).isNullOrEmpty();
                assertThat(it.httpPath()).isNullOrEmpty();
                assertThat(it.httpQuery()).isNullOrEmpty();
                assertThat(it.httpBody()).isNullOrEmpty();
                assertThat(it.action()).isEqualTo("BYPASS_ACCESS_CHECK");
                assertThat(it.sourceClass()).isEqualTo("i.g.n.x.i.BypassTestHelper");
                assertThat(it.sourceMethod()).isEqualTo("testMethod");
            });
        } else {
            assertThat(logs).isEmpty();
        }
    }

}
