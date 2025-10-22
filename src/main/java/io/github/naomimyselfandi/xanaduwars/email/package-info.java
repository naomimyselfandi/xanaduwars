/// Support for sending email. Emails are sent asynchronously, with support for
/// automatic retries. Each email has a content DTO, which provides information
/// to a template describing the email body; the specific template is specified
/// by the DTO's class.
@NonNullApi
package io.github.naomimyselfandi.xanaduwars.email;

import org.springframework.lang.NonNullApi;
