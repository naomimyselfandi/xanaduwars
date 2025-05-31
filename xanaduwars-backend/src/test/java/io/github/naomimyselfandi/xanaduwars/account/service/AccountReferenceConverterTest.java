package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountIdReference;
import io.github.naomimyselfandi.xanaduwars.account.value.CurrentAccountReference;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.convert.ConversionFailedException;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class AccountReferenceConverterTest {

    private AccountReferenceConverter fixture;

    @BeforeEach
    void setup() {
        fixture = new AccountReferenceConverter();
    }

    @Test
    void convert_WhenTheStringIsAValidUUID_ThenWrapsIt(SeededRng random) {
        var uuid = random.nextUUID();
        assertThat(fixture.convert(uuid.toString())).isEqualTo(new AccountIdReference(uuid));
    }

    @Test
    void convert_WhenTheStringIsMe_ThenReturnsTheCurrentAccountReference() {
        assertThat(fixture.convert("me")).isInstanceOf(CurrentAccountReference.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "93aa1354-c6d2-4d9b-b412-306f3858d0bg"})
    void convert_WhenTheStringIsInvalid_ThenThrows(String source) {
        assertThatThrownBy(() -> fixture.convert(source)).isInstanceOf(ConversionFailedException.class);
    }

}
