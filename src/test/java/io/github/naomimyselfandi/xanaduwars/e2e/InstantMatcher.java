package io.github.naomimyselfandi.xanaduwars.e2e;

import lombok.RequiredArgsConstructor;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
class InstantMatcher extends BaseMatcher<String> {

    private final Duration duration;
    private final Instant instant;

    @Override
    public boolean matches(Object actual) {
        return Duration.between(instant, Instant.parse(actual.toString())).abs().compareTo(duration) <= 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("within %s of %s", duration, instant));
    }

}
