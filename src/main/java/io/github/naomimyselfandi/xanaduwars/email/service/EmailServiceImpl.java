package io.github.naomimyselfandi.xanaduwars.email.service;

import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.email.entity.Email;
import io.github.naomimyselfandi.xanaduwars.email.entity.EmailRepository;
import io.github.naomimyselfandi.xanaduwars.email.value.EmailContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
class EmailServiceImpl implements EmailService {

    private final Clock clock;
    private final EmailRepository emailRepository;

    @Override
    @Transactional
    public void send(EmailAddress address, EmailContent content) {
        var email = new Email()
                .setAddress(address)
                .setContent(content)
                .setStatusTime(clock.instant());
        emailRepository.save(email);
    }

}
