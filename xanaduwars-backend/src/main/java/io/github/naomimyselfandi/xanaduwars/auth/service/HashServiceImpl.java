package io.github.naomimyselfandi.xanaduwars.auth.service;

import io.github.naomimyselfandi.xanaduwars.account.value.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class HashServiceImpl implements HashService {

    private final PasswordEncoder encoder;

    @Override
    public <T extends Hash> T hash(Plaintext<T> plaintext) {
        return plaintext.toHash(encoder.encode(plaintext.text()));
    }

}
