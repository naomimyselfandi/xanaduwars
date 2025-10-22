package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.security.dto.AuditLogFilterDto;
import io.github.naomimyselfandi.xanaduwars.security.service.AuditService;
import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class AuditingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditTestHelper auditTestHelper;

    @Test
    void auditing() {
        auditTestHelper.testMethod();
        var logs = auditService.find(new AuditLogFilterDto().setAction("TEST_METHOD"), Pageable.unpaged());
        assertThat(logs).singleElement().satisfies(it -> {
            assertThat(it.id()).isNotNull();
            var fiveSeconds = new TemporalUnitWithinOffset(5, ChronoUnit.SECONDS);
            assertThat(it.timestamp()).isCloseTo(Instant.now(), fiveSeconds);
            assertThat(it.accountId()).isNull();
            assertThat(it.username()).isEqualTo("i.g.n.x.i.AuditingIntegrationTest::auditing");
            assertThat(it.httpMethod()).isNullOrEmpty();
            assertThat(it.httpPath()).isNullOrEmpty();
            assertThat(it.httpQuery()).isNullOrEmpty();
            assertThat(it.httpBody()).isNullOrEmpty();
            assertThat(it.action()).isEqualTo("TEST_METHOD");
            assertThat(it.sourceClass()).isEqualTo("i.g.n.x.i.AuditTestHelper");
            assertThat(it.sourceMethod()).isEqualTo("testMethod");
        });
    }

}
