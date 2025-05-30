package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.xanaduwars.account.value.Password;
import io.github.naomimyselfandi.xanaduwars.ext.ExcludeFromCoverageReport;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/// An account for a regular human user.
@Entity
@Getter
@Setter
@ExcludeFromCoverageReport
@DiscriminatorValue("HUMAN")
public class HumanAccount extends Account {

    private Password authenticationSecret;

    @Override
    public String toString() {
        return "Human" + super.toString();
    }

}
