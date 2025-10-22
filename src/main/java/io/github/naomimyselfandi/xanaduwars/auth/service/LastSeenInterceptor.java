package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
class LastSeenInterceptor implements HandlerInterceptor {

    private Map<Id<Account>, Instant> buffer = new ConcurrentHashMap<>();

    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final Clock clock;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        authService.loadForAuthenticatedUser().ifPresent(it -> buffer.put(it.id(), clock.instant()));
        return true;
    }

    @Scheduled(fixedRateString = "${xanadu.last-seen-at.update-interval:1M}")
    void flush() {
        var oldBuffer = buffer;
        buffer = new ConcurrentHashMap<>(buffer.size());
        oldBuffer.forEach(accountRepository::updateLastSeenAtById);
    }

}
