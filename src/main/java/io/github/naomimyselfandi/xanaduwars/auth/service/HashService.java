package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.value.*;

/// A service that handles hashing passwords and API keys.
public interface HashService {

    <T extends Hash> T hash(Plaintext<T> plaintext);

    boolean test(Plaintext<?> plaintext, Hash hash);

}
