package io.github.naomimyselfandi.xanaduwars.audit.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.audit.entity.AuditLog;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuditLogFilterDtoTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Root<AuditLog> root;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CriteriaQuery<?> criteriaQuery;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CriteriaBuilder criteriaBuilder;

    @Test
    void toSpecification_Id(SeededRng random) {
        var filter = new AuditLogFilterDto().setAccountId(random.get());
        var expected = criteriaBuilder.and(
                criteriaBuilder.equal(root.get(AuditLog.Fields.accountId), filter.getAccountId())
        );
        var actual = filter.toSpecification().toPredicate(root, criteriaQuery, criteriaBuilder);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toSpecification_HttpPath(SeededRng random) {
        var filter = new AuditLogFilterDto().setHttpPath(random.get());
        var expected = criteriaBuilder.and(
                criteriaBuilder.equal(root.get(AuditLog.Fields.httpPath), filter.getHttpPath())
        );
        var actual = filter.toSpecification().toPredicate(root, criteriaQuery, criteriaBuilder);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toSpecification_Action(SeededRng random) {
        var filter = new AuditLogFilterDto().setAction(random.get());
        var expected = criteriaBuilder.and(
                criteriaBuilder.equal(root.get(AuditLog.Fields.action), filter.getAction())
        );
        var actual = filter.toSpecification().toPredicate(root, criteriaQuery, criteriaBuilder);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toSpecification_IdAndPathAndAction(SeededRng random) {
        var filter = new AuditLogFilterDto()
                .setAccountId(random.get())
                .setHttpPath(random.get())
                .setAction(random.get());
        var expected = criteriaBuilder.and(
                criteriaBuilder.equal(root.get(AuditLog.Fields.accountId), filter.getAccountId()),
                criteriaBuilder.equal(root.get(AuditLog.Fields.httpPath), filter.getHttpPath()),
                criteriaBuilder.equal(root.get(AuditLog.Fields.action), filter.getAction())
        );
        var actual = filter.toSpecification().toPredicate(root, criteriaQuery, criteriaBuilder);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toSpecification_Before(SeededRng random) {
        var filter = new AuditLogFilterDto()
                .setBefore(random.get());
        var expected = criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get(AuditLog.Fields.timestamp), filter.getBefore())
        );
        var actual = filter.toSpecification().toPredicate(root, criteriaQuery, criteriaBuilder);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toSpecification_After(SeededRng random) {
        var filter = new AuditLogFilterDto()
                .setAfter(random.get());
        var expected = criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(root.get(AuditLog.Fields.timestamp), filter.getAfter())
        );
        var actual = filter.toSpecification().toPredicate(root, criteriaQuery, criteriaBuilder);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toSpecification_TimeRange(SeededRng random) {
        var filter = new AuditLogFilterDto()
                .setBefore(random.get())
                .setAfter(random.get());
        var expected = criteriaBuilder.and(new Predicate[]{
                criteriaBuilder.lessThanOrEqualTo(root.get(AuditLog.Fields.timestamp), filter.getBefore()),
                criteriaBuilder.greaterThanOrEqualTo(root.get(AuditLog.Fields.timestamp), filter.getAfter())
        });
        var actual = filter.toSpecification().toPredicate(root, criteriaQuery, criteriaBuilder);
        assertThat(actual).isEqualTo(expected);
    }

}
