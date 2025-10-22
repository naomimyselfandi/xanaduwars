package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.util.Id;

/// The main service for working with accounts.
public interface AccountService {

    /// Fetch an account by ID.
    <T extends AccountDto> T getAccount(Id<Account> id, Class<T> type);

}
