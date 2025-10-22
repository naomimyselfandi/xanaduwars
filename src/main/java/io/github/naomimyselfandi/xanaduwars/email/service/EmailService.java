package io.github.naomimyselfandi.xanaduwars.email.service;

import io.github.naomimyselfandi.xanaduwars.account.value.EmailAddress;
import io.github.naomimyselfandi.xanaduwars.email.value.EmailContent;

/// A service for sending emails.
public interface EmailService {

    /// Asynchronously send an email.
    void send(EmailAddress address, EmailContent content);

}
