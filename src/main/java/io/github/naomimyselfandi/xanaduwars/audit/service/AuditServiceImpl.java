package io.github.naomimyselfandi.xanaduwars.audit.service;

import io.github.naomimyselfandi.xanaduwars.audit.Audited;
import io.github.naomimyselfandi.xanaduwars.audit.dto.AuditLogDto;
import io.github.naomimyselfandi.xanaduwars.audit.dto.AuditLogFilterDto;
import io.github.naomimyselfandi.xanaduwars.audit.entity.AuditLog;
import io.github.naomimyselfandi.xanaduwars.audit.entity.AuditLogRepository;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.util.FilterDtoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class AuditServiceImpl implements AuditService {

    private final @Lazy AuditService self;
    private final AuthService authService;
    private final FilterDtoService filterDtoService;
    private final AuditLogRepository auditLogRepository;
    private final Converter<AuditLog, AuditLogDto> converter;

    @Override
    @Audited("AUDIT_READ")
    public Page<AuditLogDto> find(AuditLogFilterDto filter, Pageable pageable) {
        var specification = filterDtoService.toSpecification(filter);
        return auditLogRepository.findAll(specification, pageable).map(converter::convert);
    }

    @Override
    public void log(String action) {
        self.log(action, null, Audited.MissingRequestPolicy.WRITE_INCOMPLETE_ENTRY_AND_WARN);
    }

    @Override
    public void log(String action, @Nullable Method method) {
        self.log(action, method, Audited.MissingRequestPolicy.WRITE_INCOMPLETE_ENTRY_AND_WARN);
    }

    @Override
    @Transactional
    public void log(String action, @Nullable Method method, Audited.MissingRequestPolicy missingRequestPolicy) {
        var auditLog = new AuditLog().setAction(action);
        try {
            var account = authService.tryGet().orElse(null);
            if (account != null) {
                auditLog.setAccountId(account.getId()).setUsername(account.getUsername());
            } else if (shouldWriteIncompleteEntry(method, missingRequestPolicy)) {
                auditLog.setUsername(findMostLikelySource());
            } else {
                return;
            }
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                var request = attributes.getRequest();
                auditLog.setHttpMethod(request.getMethod())
                        .setHttpPath(request.getPathInfo())
                        .setHttpQuery(request.getQueryString());
                if (request instanceof ContentCachingRequestWrapper wrapper) {
                    auditLog.setHttpBody(wrapper.getContentAsString());
                }
            }
            if (method != null) {
                auditLog.setSourceClass(abbreviate(method.getDeclaringClass().getName()))
                        .setSourceMethod(method.getName());
            } else {
                var element = Thread.currentThread().getStackTrace()[2];
                auditLog.setSourceClass(abbreviate(element.getClassName()))
                        .setSourceMethod(element.getMethodName());
            }
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed saving audit log entry {}!", auditLog, e);
        }
        log.info("Wrote audit log entry {}.", auditLog);
    }

    private static @Nullable String findMostLikelySource() {
        return List
                .of(Thread.currentThread().getStackTrace())
                .reversed()
                .stream()
                .filter(it -> it.getClassName().contains(".xanaduwars."))
                .findFirst()
                .map(it -> "%s::%s".formatted(abbreviate(it.getClassName()), it.getMethodName()))
                .orElse(null);
    }

    private boolean shouldWriteIncompleteEntry(@Nullable Method method, Audited.MissingRequestPolicy policy) {
        return switch (policy) {
            case SKIP -> false;
            case WRITE_INCOMPLETE_ENTRY -> true;
            case WRITE_INCOMPLETE_ENTRY_AND_WARN -> {
                log.warn("Got call to audited method {} outside of an HTTP context.", method);
                yield true;
            }
        };
    }

    private static String abbreviate(String className) {
        return className.replaceAll("([a-z])\\w+\\.", "$1.");
    }

}
