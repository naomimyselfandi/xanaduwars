package io.github.naomimyselfandi.xanaduwars.auth.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
class UserDetailsDtoConverter implements Converter<Account, UserDetailsDto> {

    @Override
    public UserDetailsDto convert(Account source) {
        var dto = new UserDetailsDto();
        dto.setId(source.getId());
        dto.setUsername(source.getUsername().username());
        dto.setPassword(source.getPassword().text());
        dto.setAuthorities(Arrays.stream(Role.values()).filter(source::hasRole).toList());
        return dto;
    }

}
