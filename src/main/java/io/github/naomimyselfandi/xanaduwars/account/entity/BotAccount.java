package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.xanaduwars.account.value.APIKey;
import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/// A bot account with an API key.
@Entity
@Getter
@Setter
@ExcludeFromCoverageReport
@DiscriminatorValue("BOT")
public class BotAccount extends Account {

    private APIKey authenticationSecret;

    @Override
    public String toString() {
        return "Bot" + super.toString();
    }

}
