package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class AccountDtoTest {

    @RepeatedTest(3)
    void mapping(SeededRng random) {
        var account = new HumanAccount()
                .id(random.nextUUID())
                .username(random.nextUsername())
                .createdAt(Instant.ofEpochMilli(random.nextLong()))
                .lastSeenAt(Instant.ofEpochMilli(random.nextLong()))
                .admin(random.nextBoolean())
                .moderator(random.nextBoolean())
                .support(random.nextBoolean())
                .judge(random.nextBoolean());
        var dto = new AccountDto(account);
        assertThat(dto.getId()).isEqualTo(account.id());
        assertThat(dto.getUsername()).isEqualTo(account.username());
        assertThat(dto.getCreatedAt()).isEqualTo(account.createdAt());
        assertThat(dto.getLastSeenAt()).isEqualTo(account.lastSeenAt());
        assertThat(dto.isAdmin()).isEqualTo(account.admin());
        assertThat(dto.isModerator()).isEqualTo(account.moderator());
        assertThat(dto.isSupport()).isEqualTo(account.support());
        assertThat(dto.isJudge()).isEqualTo(account.judge());
    }

}
