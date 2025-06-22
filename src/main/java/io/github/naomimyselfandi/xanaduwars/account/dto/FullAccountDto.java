package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import lombok.Data;
import lombok.EqualsAndHashCode;

/// A DTO representing all of an account's details, other than its password and
/// API key.
@Data
@EqualsAndHashCode(callSuper = true)
public class FullAccountDto extends BaseAccountDto implements AccountDto {
    private EmailAddress emailAddress;
    private AccountSettingsDto settings;
}
