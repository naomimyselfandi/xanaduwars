package io.github.naomimyselfandi.xanaduwars.audit.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.audit.entity.AuditLog;
import io.github.naomimyselfandi.xanaduwars.util.FilterDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import jakarta.persistence.criteria.Predicate;
import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;

/// Filtering options for audit logs.
@Data
public class AuditLogFilterDto implements FilterDto<AuditLog> {

    /// The account ID to filter by.
    private @Nullable Id<Account> accountId;

    private @Nullable String httpPath;

    private @Nullable String action;

    private @Nullable Instant before;

    private @Nullable Instant after;

    @Override
    public Specification<AuditLog> toSpecification() {
        return (root, _, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (accountId != null) {
                predicates.add(criteriaBuilder.equal(root.get(AuditLog.Fields.accountId), accountId));
            }
            if (httpPath != null) {
                predicates.add(criteriaBuilder.equal(root.get(AuditLog.Fields.httpPath), httpPath));
            }
            if (action != null) {
                predicates.add(criteriaBuilder.equal(root.get(AuditLog.Fields.action), action));
            }
            if (before != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(AuditLog.Fields.timestamp), before));
            }
            if (after != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(AuditLog.Fields.timestamp), after));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

}
