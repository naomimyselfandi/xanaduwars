package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;

import java.util.Optional;

/// A service that resolves account references.
public interface AccountReferenceResolver {

    /// Resolve an account reference.
    <T extends AccountDto> Optional<T> resolve(Class<T> dto, AccountReference accountReference);

}
