package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.xanaduwars.account.value.APIKey;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/// A bot account with an API key.
@Entity
@Getter
@Setter
@DiscriminatorValue("BOT")
public class BotAccount extends Account {

    private APIKey authenticationSecret;

    @Override
    public String toString() {
        return "Bot" + super.toString();
    }

}
