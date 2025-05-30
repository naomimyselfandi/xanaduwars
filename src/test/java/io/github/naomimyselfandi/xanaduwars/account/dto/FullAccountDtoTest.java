package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountSettings;
import io.github.naomimyselfandi.xanaduwars.account.entity.HumanAccount;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeededRandomExtension.class)
class FullAccountDtoTest {

    @RepeatedTest(3)
    void mapping(SeededRng random) {
        var settings = new AccountSettings()
                .emailNotifications(random.nextBoolean())
                .allowMagicLinks(random.nextBoolean())
                .hideActivity(random.nextBoolean())
                .timezone(ZoneId.of(random.pick(ZoneId.getAvailableZoneIds())));
        var account = new HumanAccount()
                .id(random.nextUUID())
                .username(random.nextUsername())
                .emailAddress(random.nextEmailAddress())
                .settings(settings)
                .createdAt(Instant.ofEpochMilli(random.nextLong()))
                .lastSeenAt(Instant.ofEpochMilli(random.nextLong()))
                .admin(random.nextBoolean())
                .moderator(random.nextBoolean())
                .support(random.nextBoolean())
                .judge(random.nextBoolean());
        var dto = new FullAccountDto(account);
        assertThat(dto.getId()).isEqualTo(account.id());
        assertThat(dto.getUsername()).isEqualTo(account.username());
        assertThat(dto.getEmailAddress()).isEqualTo(account.emailAddress());
        assertThat(dto.getSettings()).satisfies(settingsDto -> {
            assertThat(settingsDto.isEmailNotifications()).isEqualTo(settings.emailNotifications());
            assertThat(settingsDto.isAllowMagicLinks()).isEqualTo(settings.allowMagicLinks());
            assertThat(settingsDto.isHideActivity()).isEqualTo(settings.hideActivity());
            assertThat(settingsDto.getTimezone()).isEqualTo(settings.timezone());
        });
        assertThat(dto.getCreatedAt()).isEqualTo(account.createdAt());
        assertThat(dto.getLastSeenAt()).isEqualTo(account.lastSeenAt());
        assertThat(dto.isAdmin()).isEqualTo(account.admin());
        assertThat(dto.isModerator()).isEqualTo(account.moderator());
        assertThat(dto.isSupport()).isEqualTo(account.support());
        assertThat(dto.isJudge()).isEqualTo(account.judge());
    }

}
