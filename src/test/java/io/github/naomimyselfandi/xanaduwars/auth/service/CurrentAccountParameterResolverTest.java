package io.github.naomimyselfandi.xanaduwars.auth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.BotAccount;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.auth.CurrentAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.MethodParameter;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CurrentAccountParameterResolverTest {

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private CurrentAccountService currentAccountService;

    @InjectMocks
    private CurrentAccountParameterResolver fixture;

    @ParameterizedTest
    @CsvSource(textBlock = """
            true,Account,true
            true,Optional<Account>,true
            true,HumanAccount,false
            true,BotAccount,false
            true,SomethingElse,false
            false,Account,false
            false,Optional<Account>,false
            false,SomethingElse,false
            """)
    void supportsParameter(boolean annotated, String type, boolean expected) {
        when(methodParameter.hasParameterAnnotation(CurrentAccount.class)).thenReturn(annotated);
        when(methodParameter.getGenericParameterType()).thenReturn(switch (type) {
            case "Account" -> Account.class;
            case "Optional<Account>" -> new TypeReference<Account>() {}.getType();
            case "HumanAccount" -> HumanAccount.class;
            case "BotAccount" -> BotAccount.class;
            default -> Object.class;
        });
        assertThat(fixture.supportsParameter(methodParameter)).isEqualTo(expected);
    }

    @Test
    void resolveArgument() {
        var account = new HumanAccount();
        doReturn(Account.class).when(methodParameter).getParameterType(); // Backwards to placate the type checker
        when(currentAccountService.get()).thenReturn(account);
        assertThat(fixture.resolveArgument(methodParameter, mock(), mock(), mock())).isEqualTo(account);
    }

    @Test
    void resolveArgument_Optional() {
        var account = new HumanAccount();
        doReturn(Optional.class).when(methodParameter).getParameterType(); // Backwards to placate the type checker
        when(currentAccountService.tryGet()).thenReturn(Optional.of(account));
        assertThat(fixture.resolveArgument(methodParameter, mock(), mock(), mock())).isEqualTo(Optional.of(account));
    }

}
