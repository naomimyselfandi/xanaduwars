package io.github.naomimyselfandi.xanaduwars.email.service;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.email.entity.Email;
import io.github.naomimyselfandi.xanaduwars.email.entity.EmailRepository;
import io.github.naomimyselfandi.xanaduwars.email.value.EmailContent;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class EmailServiceImplTest {

    @Captor
    private ArgumentCaptor<Email> captor;

    @Mock
    private Clock clock;

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private EmailServiceImpl fixture;

    @Test
    void send(SeededRng random) {
        var address = random.<EmailAddress>get();
        var content = random.<EmailContent>get();
        var instant = random.nextInstant();
        when(clock.instant()).thenReturn(instant);
        fixture.send(address, content);
        verify(emailRepository).save(captor.capture());
        var email = captor.getValue();
        assertThat(email.getAddress()).isEqualTo(address);
        assertThat(email.getContent()).isEqualTo(content);
        assertThat(email.getAttempts()).isZero();
        assertThat(email.getStatus()).isEqualTo(Email.Status.PENDING);
        assertThat(email.getStatusTime()).isEqualTo(instant);
        assertThat(email.getStatusReason()).isEmpty();
    }

}
