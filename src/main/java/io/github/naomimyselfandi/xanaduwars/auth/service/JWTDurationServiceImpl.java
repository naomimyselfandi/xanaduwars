package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.value.JWTPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
class JWTDurationServiceImpl implements JWTDurationService {

    private final AuthConfiguration authConfiguration;

    @Override
    public Duration getDuration(UserDetailsDto dto, JWTPurpose purpose) {
        return switch (purpose) {
            case ACCESS_TOKEN -> authConfiguration.getAccessTokenDuration();
            case REFRESH_TOKEN -> dto.rememberMe()
                    ? authConfiguration.getRefreshTokenLongDuration()
                    : authConfiguration.getRefreshTokenShortDuration();
            case PASSWORD_RESET -> authConfiguration.getPasswordResetDuration();
        };
    }

}
