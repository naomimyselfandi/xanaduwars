package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;
import java.util.UUID;

import static io.github.naomimyselfandi.xanaduwars.ext.FieldIteration.forEachField;

/// The basic DTO representing an account.
@Data
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
public class AccountDto {

    private UUID id;
    private Username username;
    private Instant createdAt, lastSeenAt;
    private boolean admin, moderator, support, judge, developer;

    /// Mapping constructor.
    public AccountDto(Account source) {
        forEachField(Fields.values(), field -> switch (field) {
            case id -> id = source.id();
            case username -> username = source.username();
            case createdAt -> createdAt = source.createdAt();
            case lastSeenAt -> lastSeenAt = source.lastSeenAt();
            case admin -> admin = source.admin();
            case moderator -> moderator = source.moderator();
            case support -> support = source.support();
            case judge -> judge = source.judge();
            case developer -> developer = source.developer();
        });
    }

}
