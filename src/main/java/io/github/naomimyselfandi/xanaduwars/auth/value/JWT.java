package io.github.naomimyselfandi.xanaduwars.auth.value;

import java.time.Duration;

/// A JWT with a duration.
public record JWT(String token, Duration duration) {}
