package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.AccountSettings;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.ZoneId;

import static io.github.naomimyselfandi.xanaduwars.ext.FieldIteration.forEachField;

/// A DTO representing an account's user-configurable settings.
@Data
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public class AccountSettingsDto {

    private boolean emailNotifications;
    private boolean allowMagicLinks;
    private boolean hideActivity;
    private ZoneId timezone;

    /// Mapping constructor.
    public AccountSettingsDto(AccountSettings source) {
        forEachField(Fields.values(), field -> switch (field) {
            case emailNotifications -> emailNotifications = source.emailNotifications();
            case allowMagicLinks -> allowMagicLinks = source.allowMagicLinks();
            case hideActivity -> hideActivity = source.hideActivity();
            case timezone -> timezone = source.timezone();
        });
    }

}
