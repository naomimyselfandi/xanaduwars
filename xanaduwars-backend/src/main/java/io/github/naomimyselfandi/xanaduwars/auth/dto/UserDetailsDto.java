package io.github.naomimyselfandi.xanaduwars.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.naomimyselfandi.xanaduwars.account.dto.AccountDto;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountId;
import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
public class UserDetailsDto implements UserDetails, AccountDto {
    private AccountId id;
    private String username;
    private @JsonIgnore String password;
    private List<Role> authorities;
}
