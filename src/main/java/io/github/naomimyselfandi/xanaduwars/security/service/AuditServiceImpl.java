package io.github.naomimyselfandi.xanaduwars.security.service;

import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.security.Audited;
import io.github.naomimyselfandi.xanaduwars.security.dto.AuditLogDto;
import io.github.naomimyselfandi.xanaduwars.security.entity.AuditLog;
import io.github.naomimyselfandi.xanaduwars.security.entity.AuditLogRepository;
import io.github.naomimyselfandi.xanaduwars.util.Cleanup;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
class AuditServiceImpl implements AuditService {

    private static final Cleanup NOOP = () -> {};

    private final ThreadLocal<Boolean> suppressed = ThreadLocal.withInitial(() -> false);

    private final Cleanup unsuppress = suppressed::remove;

    private final AuthService authService;
    private final AuditLogRepository auditLogRepository;
    private final Converter<AuditLog, AuditLogDto> converter;

    @Override
    @Audited("AUDIT_READ")
    public Page<AuditLogDto> find(Specification<AuditLog> specification, Pageable pageable) {
        return auditLogRepository.findAll(specification, pageable).map(converter::convert);
    }

    @Override
    public Cleanup suppress() {
        if (suppressed.get()) {
            return NOOP;
        } else {
            suppressed.set(true);
            return unsuppress;
        }
    }

    @Override
    @Transactional
    public void log(String action, @Nullable Method method, Audited.MissingAuthPolicy missingAuthPolicy) {
        if (suppressed.get()) return;
        var auditLog = new AuditLog().setAction(action);
        try {
            var account = authService.loadForAuthenticatedUser().orElse(null);
            if (account != null) {
                auditLog.setAccountId(account.id()).setUsername(account.username().toString());
            } else if (shouldWriteIncompleteEntry(method, missingAuthPolicy)) {
                auditLog.setUsername(findMostLikelySource());
            } else {
                return;
            }
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                var request = attributes.getRequest();
                auditLog.setHttpMethod(request.getMethod())
                        .setHttpPath(request.getPathInfo())
                        .setHttpQuery(Objects.requireNonNullElse(request.getQueryString(), ""))
                        .setHttpBody(getContent(request));
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
            log.info("Wrote audit log entry {}.", auditLog);
        } catch (Exception e) {
            log.error("Failed saving audit log entry {}!", auditLog, e);
        }
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

    private boolean shouldWriteIncompleteEntry(@Nullable Method method, Audited.MissingAuthPolicy policy) {
        return switch (policy) {
            case WRITE_NOTHING -> false;
            case WRITE_INCOMPLETE_ENTRY -> true;
            case WRITE_INCOMPLETE_ENTRY_AND_WARN -> {
                log.warn("Got call to audited method {} with no authenticated user.", method);
                yield true;
            }
        };
    }

    private static String abbreviate(String className) {
        return className.replaceAll("([a-z])\\w+\\.", "$1.");
    }

    private static @Nullable String getContent(ServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            return wrapper.getContentAsString();
        } else if (request instanceof HttpServletRequestWrapper wrapper) {
            return getContent(wrapper.getRequest());
        } else {
            return null;
        }
    }

}
