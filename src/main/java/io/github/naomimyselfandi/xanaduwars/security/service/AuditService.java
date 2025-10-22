package io.github.naomimyselfandi.xanaduwars.security.service;

import io.github.naomimyselfandi.xanaduwars.security.Audited;
import io.github.naomimyselfandi.xanaduwars.security.dto.AuditLogDto;
import io.github.naomimyselfandi.xanaduwars.security.entity.AuditLog;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Method;

/// The main service for working with audit logs.
public interface AuditService {

    /// Find a page of audit logs matching the given criteria.
    Page<AuditLogDto> find(Specification<AuditLog> specification, Pageable pageable);

    /// Create an audit log entry. If the audited method is not given, it is
    /// assumed to be the method from which this method is called.
    void log(String action, @Nullable Method method, Audited.MissingAuthPolicy ifUnauthenticated);

}
