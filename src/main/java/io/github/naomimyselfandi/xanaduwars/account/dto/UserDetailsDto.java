package io.github.naomimyselfandi.xanaduwars.account.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.account.value.Password;
import io.github.naomimyselfandi.xanaduwars.account.value.RoleSet;
import io.github.naomimyselfandi.xanaduwars.account.value.Username;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/// A DTO representing an authenticated user.
@JsonIgnoreProperties({
        "accountNonExpired",
        "accountNonLocked",
        "credentialsNonExpired",
        "enabled",
        "password",
        "authorities"
})
@Builder(toBuilder = true)
public record UserDetailsDto(
        Id<Account> id,
        Username username,
        EmailAddress emailAddress,
        Password password,
        RoleSet roles,
        boolean rememberMe
) implements UserDetails, AccountDto {

    @Override
    public String getUsername() {
        return username.username();
    }

    @Override
    public String getPassword() {
        return password.password();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.asCollection();
    }

}
