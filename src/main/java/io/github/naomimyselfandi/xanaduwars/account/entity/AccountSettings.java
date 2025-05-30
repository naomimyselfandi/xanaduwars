package io.github.naomimyselfandi.xanaduwars.account.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.ZoneId;

/// An account's user-configurable settings.
@Data
@Embeddable
public class AccountSettings {

    private static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("UTC");

    /// Whether the user has opted into email notifications.
    private boolean emailNotifications;

    /// Whether the user has opted into magic links.
    private boolean allowMagicLinks;

    /// Whether the user has chosen to hide their online status.
    private boolean hideActivity;

    /// The user's time zone.
    private ZoneId timezone = DEFAULT_TIMEZONE;

}
