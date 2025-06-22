package io.github.naomimyselfandi.xanaduwars.audit.service;

import io.github.naomimyselfandi.xanaduwars.audit.Audited;
import io.github.naomimyselfandi.xanaduwars.audit.dto.AuditLogDto;
import io.github.naomimyselfandi.xanaduwars.audit.dto.AuditLogFilterDto;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Method;

/// The main service for working with audit logs.
public interface AuditService {

    /// Find a page of audit logs matching the given criteria.
    Page<AuditLogDto> find(AuditLogFilterDto filter, Pageable pageable);

    /// Create an audit log entry. The audited method is assumed to be the
    /// method from which this method is called.
    void log(String action);

    /// Create an audit log entry. If the audited method is not given, it is
    /// assumed to be the method from which this method is called.
    void log(String action, @Nullable Method method);

    /// Create an audit log entry. If the audited method is not given, it is
    /// assumed to be the method from which this method is called.
    void log(String action, @Nullable Method method, Audited.MissingRequestPolicy missingRequestPolicy);

}
