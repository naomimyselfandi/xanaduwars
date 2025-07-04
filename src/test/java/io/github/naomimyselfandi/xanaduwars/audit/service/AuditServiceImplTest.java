package io.github.naomimyselfandi.xanaduwars.audit.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.audit.Audited;
import io.github.naomimyselfandi.xanaduwars.audit.dto.AuditLogDto;
import io.github.naomimyselfandi.xanaduwars.audit.dto.AuditLogFilterDto;
import io.github.naomimyselfandi.xanaduwars.audit.entity.AuditLog;
import io.github.naomimyselfandi.xanaduwars.audit.entity.AuditLogRepository;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.service.AuthService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.FilterDtoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static io.github.naomimyselfandi.xanaduwars.audit.Audited.MissingRequestPolicy.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuditServiceImplTest {

    @Mock
    private Specification<AuditLog> specification;

    @Mock
    private Pageable pageable;

    @Mock
    private ServletRequestAttributes attributes;

    @Mock
    private ContentCachingRequestWrapper contentCachingRequestWrapper;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private AuditService self;

    @Mock
    private AuthService authService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private FilterDtoService filterDtoService;

    @Mock
    private Converter<AuditLog, AuditLogDto> converter;

    @InjectMocks
    private AuditServiceImpl fixture;

    @Captor
    private ArgumentCaptor<AuditLog> captor;

    @Test
    void find(SeededRng random) {
        var filter = random.<AuditLogFilterDto>get();
        when(filterDtoService.toSpecification(filter)).thenReturn(specification);
        var log = random.<AuditLog>get();
        var out = random.<AuditLogDto>get();
        when(converter.convert(log)).thenReturn(out);
        when(auditLogRepository.findAll(specification, pageable)).thenReturn(new PageImpl<>(List.of(log)));
        assertThat(fixture.find(filter, pageable)).isEqualTo(new PageImpl<>(List.of(out)));
    }

    @Test
    void log_WithDefaultMethod(SeededRng random) {
        var action = random.nextString();
        fixture.log(action);
        verify(self).log(action, null, WRITE_INCOMPLETE_ENTRY_AND_WARN);
    }

    @Test
    void log_WithDefaultMissingRequestPolicy(SeededRng random) {
        var action = random.nextString();
        var method = getMethod();
        fixture.log(action, method);
        verify(self).log(action, method, WRITE_INCOMPLETE_ENTRY_AND_WARN);
    }

    @EnumSource
    @ParameterizedTest
    void log(Audited.MissingRequestPolicy policy, SeededRng random) {
        var account = logAccount(random);
        var action = random.nextString();
        var method = getMethod();
        var httpMethod = random.nextString();
        var httpPath = random.nextString();
        var httpQuery = random.nextString();
        var httpBody = random.nextString();
        RequestContextHolder.setRequestAttributes(attributes);
        try {
            when(attributes.getRequest()).thenReturn(contentCachingRequestWrapper);
            when(contentCachingRequestWrapper.getMethod()).thenReturn(httpMethod);
            when(contentCachingRequestWrapper.getPathInfo()).thenReturn(httpPath);
            when(contentCachingRequestWrapper.getQueryString()).thenReturn(httpQuery);
            when(contentCachingRequestWrapper.getContentAsString()).thenReturn(httpBody);
            assertThatCode(() -> fixture.log(action, method, policy)).doesNotThrowAnyException();
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
        verify(auditLogRepository).save(captor.capture());
        var auditLog = captor.getValue();
        assertThat(auditLog.getAccountId()).isEqualTo(account.getId());
        assertThat(auditLog.getUsername()).isEqualTo(account.getUsername().toString());
        assertThat(auditLog.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(auditLog.getHttpPath()).isEqualTo(httpPath);
        assertThat(auditLog.getHttpQuery()).isEqualTo(httpQuery);
        assertThat(auditLog.getHttpBody()).isEqualTo(httpBody);
        assertThat(auditLog.getAction()).isEqualTo(action);
        assertThat(auditLog.getSourceClass()).isEqualTo("i.g.n.x.a.s.AuditServiceImplTest");
        assertThat(auditLog.getSourceMethod()).isEqualTo("getMethod");
    }

    @EnumSource
    @ParameterizedTest
    void log_ToleratesMissingMethod(Audited.MissingRequestPolicy policy, SeededRng random) {
        var account = logAccount(random);
        var action = random.nextString();
        var httpMethod = random.nextString();
        var httpPath = random.nextString();
        var httpQuery = random.nextString();
        var httpBody = random.nextString();
        RequestContextHolder.setRequestAttributes(attributes);
        try {
            when(attributes.getRequest()).thenReturn(contentCachingRequestWrapper);
            when(contentCachingRequestWrapper.getMethod()).thenReturn(httpMethod);
            when(contentCachingRequestWrapper.getPathInfo()).thenReturn(httpPath);
            when(contentCachingRequestWrapper.getQueryString()).thenReturn(httpQuery);
            when(contentCachingRequestWrapper.getContentAsString()).thenReturn(httpBody);
            fixture.log(action, null, policy);
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
        verify(auditLogRepository).save(captor.capture());
        var auditLog = captor.getValue();
        assertThat(auditLog.getAccountId()).isEqualTo(account.getId());
        assertThat(auditLog.getUsername()).isEqualTo(account.getUsername().toString());
        assertThat(auditLog.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(auditLog.getHttpPath()).isEqualTo(httpPath);
        assertThat(auditLog.getHttpQuery()).isEqualTo(httpQuery);
        assertThat(auditLog.getHttpBody()).isEqualTo(httpBody);
        assertThat(auditLog.getAction()).isEqualTo(action);
        assertThat(auditLog.getSourceClass()).isEqualTo("i.g.n.x.a.s.AuditServiceImplTest");
        assertThat(auditLog.getSourceMethod()).isEqualTo("log_ToleratesMissingMethod");
    }

    @EnumSource
    @ParameterizedTest
    void log_ToleratesUnexpectedRequest(Audited.MissingRequestPolicy policy, SeededRng random) throws IOException {
        var account = logAccount(random);
        var action = random.nextString();
        var method = getMethod();
        var httpMethod = random.nextString();
        var httpPath = random.nextString();
        var httpQuery = random.nextString();
        RequestContextHolder.setRequestAttributes(attributes);
        try {
            when(attributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getMethod()).thenReturn(httpMethod);
            when(httpServletRequest.getPathInfo()).thenReturn(httpPath);
            when(httpServletRequest.getQueryString()).thenReturn(httpQuery);
            assertThatCode(() -> fixture.log(action, method, policy)).doesNotThrowAnyException();
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
        verify(auditLogRepository).save(captor.capture());
        var auditLog = captor.getValue();
        assertThat(auditLog.getAccountId()).isEqualTo(account.getId());
        assertThat(auditLog.getUsername()).isEqualTo(account.getUsername().toString());
        assertThat(auditLog.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(auditLog.getHttpPath()).isEqualTo(httpPath);
        assertThat(auditLog.getHttpQuery()).isEqualTo(httpQuery);
        assertThat(auditLog.getHttpBody()).isNull();
        assertThat(auditLog.getAction()).isEqualTo(action);
        assertThat(auditLog.getSourceClass()).isEqualTo("i.g.n.x.a.s.AuditServiceImplTest");
        assertThat(auditLog.getSourceMethod()).isEqualTo("getMethod");
        verify(httpServletRequest, never()).getInputStream();
    }

    @EnumSource
    @ParameterizedTest
    void log_ToleratesMissingRequest(Audited.MissingRequestPolicy policy, SeededRng random) {
        var account = logAccount(random);
        var action = random.nextString();
        var method = getMethod();
        assertThatCode(() -> fixture.log(action, method, policy)).doesNotThrowAnyException();
        verify(auditLogRepository).save(captor.capture());
        var auditLog = captor.getValue();
        assertThat(auditLog.getAccountId()).isEqualTo(account.getId());
        assertThat(auditLog.getUsername()).isEqualTo(account.getUsername().toString());
        assertThat(auditLog.getHttpMethod()).isNull();
        assertThat(auditLog.getHttpPath()).isNull();
        assertThat(auditLog.getHttpQuery()).isNull();
        assertThat(auditLog.getHttpBody()).isNull();
        assertThat(auditLog.getAction()).isEqualTo(action);
        assertThat(auditLog.getSourceClass()).isEqualTo("i.g.n.x.a.s.AuditServiceImplTest");
        assertThat(auditLog.getSourceMethod()).isEqualTo("getMethod");
    }

    @EnumSource
    @ParameterizedTest
    void log_ToleratesMissingAccount(Audited.MissingRequestPolicy policy, SeededRng random) {
        var action = random.nextString();
        var method = getMethod();
        var httpMethod = random.nextString();
        var httpPath = random.nextString();
        var httpQuery = random.nextString();
        var httpBody = random.nextString();
        RequestContextHolder.setRequestAttributes(attributes);
        try {
            when(attributes.getRequest()).thenReturn(contentCachingRequestWrapper);
            when(contentCachingRequestWrapper.getMethod()).thenReturn(httpMethod);
            when(contentCachingRequestWrapper.getPathInfo()).thenReturn(httpPath);
            when(contentCachingRequestWrapper.getQueryString()).thenReturn(httpQuery);
            when(contentCachingRequestWrapper.getContentAsString()).thenReturn(httpBody);
            assertThatCode(() -> fixture.log(action, method, policy)).doesNotThrowAnyException();
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
        if (policy == Audited.MissingRequestPolicy.SKIP) {
            verify(auditLogRepository, never()).save(any());
        } else {
            verify(auditLogRepository).save(captor.capture());
            var auditLog = captor.getValue();
            assertThat(auditLog.getAccountId()).isNull();
            assertThat(auditLog.getUsername())
                    .isEqualTo("i.g.n.x.a.s.AuditServiceImplTest::log_ToleratesMissingAccount");
            assertThat(auditLog.getHttpMethod()).isEqualTo(httpMethod);
            assertThat(auditLog.getHttpPath()).isEqualTo(httpPath);
            assertThat(auditLog.getHttpQuery()).isEqualTo(httpQuery);
            assertThat(auditLog.getHttpBody()).isEqualTo(httpBody);
            assertThat(auditLog.getAction()).isEqualTo(action);
            assertThat(auditLog.getSourceClass()).isEqualTo("i.g.n.x.a.s.AuditServiceImplTest");
            assertThat(auditLog.getSourceMethod()).isEqualTo("getMethod");
        }
    }

    @Test
    void log_WhenSavingTheLogEntryFails_ThenDoesNotThrow(SeededRng random) {
        when(auditLogRepository.save(any())).thenThrow(RuntimeException.class);
        assertThatCode(() -> fixture.log(random.nextString(), null, WRITE_INCOMPLETE_ENTRY))
                .doesNotThrowAnyException();
    }

    private Account logAccount(SeededRng random) {
        var account = random.<Account>get();
        var details = new UserDetailsDto().setId(account.getId()).setUsername(account.getUsername().username());
        when(authService.tryGet()).thenReturn(Optional.of(details));
        return account;
    }

    @SneakyThrows
    private Method getMethod() {
        return AuditServiceImplTest.class.getDeclaredMethod("getMethod");
    }

}
