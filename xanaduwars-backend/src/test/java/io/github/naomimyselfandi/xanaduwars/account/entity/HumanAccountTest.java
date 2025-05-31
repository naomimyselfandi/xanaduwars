package io.github.naomimyselfandi.xanaduwars.account.entity;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class HumanAccountTest {

    @Test
    void testToString(SeededRng random) {
        var account = (Account) new HumanAccount();
        account.id(random.nextUUID()).username(random.nextUsername());
        var template = "HumanAccount[id=%s, username=%s]";
        assertThat(account).hasToString(template, account.id(), account.username());
    }

}
