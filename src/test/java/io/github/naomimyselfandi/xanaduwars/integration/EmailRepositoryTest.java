package io.github.naomimyselfandi.xanaduwars.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.xanaduwars.email.entity.Email;
import io.github.naomimyselfandi.xanaduwars.email.entity.EmailRepository;
import io.github.naomimyselfandi.xanaduwars.email.value.EmailContent;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class EmailRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private EmailRepository fixture;

    @Autowired
    private ObjectMapper objectMapper;

    private final Instant now = Instant.now();

    @RepeatedTest(3)
    void claimEmails() {
        var readyToSend = createEmail(Email.Status.PENDING, Duration.ofMinutes(-1));
        var sendLater = createEmail(Email.Status.PENDING, Duration.ofMinutes(1));
        var alreadySent = createEmail(Email.Status.SENT, Duration.ofHours(random.nextInt(-100, -1)));
        var alreadyFailed = createEmail(Email.Status.FAILED, Duration.ofHours(random.nextInt(-100, -1)));
        startNewTransaction();
        assertThat(fixture.claimEmails(5)).singleElement().satisfies(it -> {
            assertThat(it.id()).isEqualTo(readyToSend.getId().id());
            assertThat(it.address()).isEqualTo(readyToSend.getAddress().emailAddress());
            assertThat(it.attempts()).isEqualTo(readyToSend.getAttempts());
            assertThat(objectMapper.readValue(it.content(), EmailContent.class)).isEqualTo(readyToSend.getContent());
        });
        assertThat(fixture.findById(readyToSend.getId())).hasValueSatisfying(hasStatus(Email.Status.SENDING));
        assertThat(fixture.findById(sendLater.getId())).hasValueSatisfying(hasStatus(Email.Status.PENDING));
        assertThat(fixture.findById(alreadySent.getId())).hasValueSatisfying(hasStatus(Email.Status.SENT));
        assertThat(fixture.findById(alreadyFailed.getId())).hasValueSatisfying(hasStatus(Email.Status.FAILED));
    }

    @RepeatedTest(3)
    void claimEmails_HandlesCrashedSendingAttempts() {
        var crashed = createEmail(Email.Status.SENDING, Duration.ofMinutes(-16));
        var pending = createEmail(Email.Status.SENDING, Duration.ofMinutes(random.nextInt(-14, -1)));
        var pendingTime = pending.getStatusTime();
        startNewTransaction();
        assertThat(fixture.claimEmails(5))
                .singleElement()
                .returns(crashed.getId().id(), EmailRepository.ClaimedEmail::id);
        startNewTransaction();
        assertThat(fixture.findById(pending.getId())).hasValueSatisfying(it -> {
            var oneSecond = new TemporalUnitLessThanOffset(1, ChronoUnit.SECONDS);
            assertThat(it.getStatusTime()).isCloseTo(pendingTime, oneSecond);
        });
    }

    @RepeatedTest(3)
    void claimEmails_AvoidsRaceConditions() throws ExecutionException, InterruptedException, TimeoutException {
        var emails = IntStream
                .range(-60, -1)
                .mapToObj(Duration::ofMinutes)
                .map(offset -> createEmail(Email.Status.PENDING, offset))
                .toList();
        startNewTransaction();
        try (var executorService = Executors.newFixedThreadPool(8)) {
            var futures = IntStream
                    .range(0, 15)
                    .<Callable<List<EmailRepository.ClaimedEmail>>>mapToObj(_ -> () -> {
                        Thread.sleep(Duration.ofMillis(random.nextInt(100)));
                        return fixture.claimEmails(4);
                    })
                    .map(executorService::submit)
                    .toList();
            var expected = emails.stream().map(Email::getId).map(Id::id).toList();
            var actual = new ArrayList<UUID>(expected.size());
            for (var it : futures) {
                var ids = it.get(10, TimeUnit.SECONDS).stream().map(EmailRepository.ClaimedEmail::id).toList();
                actual.addAll(ids);
            }
            assertThat(actual).containsOnlyOnceElementsOf(expected);
        }
    }

    @RepeatedTest(3)
    void recordSendingAttempt() {
        var email = save(fixture, random.get());
        var status = random.not(email.getStatus());
        var statusTime = random.nextInstant();
        var statusReason = random.nextString();
        var attempts = email.getAttempts() + 1;
        startNewTransaction();
        fixture.recordSendingAttempt(email.getId(), status, statusReason, statusTime);
        startNewTransaction();
        email = fixture.findById(email.getId()).orElseThrow();
        assertThat(email.getStatus()).isEqualTo(status);
        assertThat(email.getStatusTime()).isEqualTo(statusTime);
        assertThat(email.getStatusReason()).isEqualTo(statusReason);
        assertThat(email.getAttempts()).isEqualTo(attempts);
    }

    private Email createEmail(Email.Status status, Duration offset) {
        return save(fixture, random.<Email>get().setStatus(status).setStatusTime(now.plus(offset)));
    }

    private static Consumer<Email> hasStatus(Email.Status status) {
        return it -> assertThat(it.getStatus()).isEqualTo(status);
    }

}
