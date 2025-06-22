package io.github.naomimyselfandi.xanaduwars.account.dto;

import lombok.Data;

import java.time.ZoneId;

/// A DTO representing an account's user-configurable settings.
@Data
public class AccountSettingsDto {
    private boolean emailNotifications;
    private boolean allowMagicLinks;
    private boolean hideActivity;
    private ZoneId timezone;
}
