package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;

/// A service that creates user accounts.
public interface RegistrationService {

    /// Register a new user account.
    /// @apiNote This method does not indicate whether the account was
    /// successfully registered to prevent username enumeration.
    void register(Username username, EmailAddress emailAddress);

}
