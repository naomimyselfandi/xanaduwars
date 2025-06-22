package io.github.naomimyselfandi.xanaduwars.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
public class UserDetailsDto implements UserDetails, AccountDto {
    private Id<Account> id;
    private String username;
    private @JsonIgnore @Nullable String password;
    private List<Role> authorities;
}
