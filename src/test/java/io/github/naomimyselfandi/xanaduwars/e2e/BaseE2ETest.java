package io.github.naomimyselfandi.xanaduwars.e2e;

import org.junit.jupiter.api.BeforeEach;

public abstract class BaseE2ETest extends AbstractE2ETest {

    @BeforeEach
    void setup(E2ETest test) {
        if (test.value()) login(createAccount(test.roles()));
    }

}
