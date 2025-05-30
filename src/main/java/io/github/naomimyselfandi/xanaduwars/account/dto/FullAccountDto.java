package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.ext.FieldIteration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/// A DTO representing all of an account's details, other than its password and
/// API key.
@Data
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
@EqualsAndHashCode(callSuper = true)
public class FullAccountDto extends AccountDto {

    private EmailAddress emailAddress;
    private AccountSettingsDto settings;

    /// Mapping constructor.
    public FullAccountDto(Account source) {
        super(source);
        FieldIteration.forEachField(Fields.values(), field -> switch (field) {
            case emailAddress -> emailAddress = source.emailAddress();
            case settings -> settings = new AccountSettingsDto(source.settings());
        });
    }

}
