package io.github.naomimyselfandi.xanaduwars.auth.jwt;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
class JWTDurationServiceImpl implements JWTDurationService {

    @Value("${xanadu.auth.jwt.access-token-duration}")
    private final Duration accessTokenDuration;

    @Value("${xanadu.auth.jwt.refresh-token-short-duration}")
    private final Duration refreshTokenShortDuration;

    @Value("${xanadu.auth.jwt.refresh-token-long-duration}")
    private final Duration refreshTokenLongDuration;

    @Value("${xanadu.auth.jwt.magic-link-duration}")
    private final Duration magicLinkDuration;

    @Override
    public Duration duration(Account account, JWTPurpose purpose, RememberMe rememberMe) {
        return switch (purpose) {
            case ACCESS_TOKEN -> accessTokenDuration;
            case REFRESH_TOKEN -> switch (rememberMe) {
                case YES -> refreshTokenLongDuration;
                case NO -> refreshTokenShortDuration;
            };
            case MAGIC_LINK -> magicLinkDuration;
        };
    }

}
