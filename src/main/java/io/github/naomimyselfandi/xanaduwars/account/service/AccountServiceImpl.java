package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import io.github.naomimyselfandi.xanaduwars.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public <T extends AccountDto> T getAccount(Id<Account> id, Class<T> type) {
        return accountRepository.findById(id, type).orElseThrow(() -> new NotFoundException(id));
    }

}
