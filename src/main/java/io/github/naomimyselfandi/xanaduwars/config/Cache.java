package io.github.naomimyselfandi.xanaduwars.config;

import lombok.experimental.UtilityClass;

/// A utility class holding the names of Spring caches.
@UtilityClass
public class Cache {

    /// A long-lived cache used to hold game versions and other configuration.
    public static final String CONFIGURATION = "configCache";

    /// A short-lived cache typically used to optimize requests.
    public static final String REQUESTS = "requestCache";

}
