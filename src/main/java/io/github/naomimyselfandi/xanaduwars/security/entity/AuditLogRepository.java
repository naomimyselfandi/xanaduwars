package io.github.naomimyselfandi.xanaduwars.security.entity;

import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/// A repository that stores audit logs.
public interface AuditLogRepository extends JpaRepository<AuditLog, Id<AuditLog>>,
        JpaSpecificationExecutor<AuditLog> {}
