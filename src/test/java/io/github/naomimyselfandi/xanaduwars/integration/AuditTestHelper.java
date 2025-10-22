package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.security.Audited;
import org.springframework.stereotype.Service;

@Service
class AuditTestHelper {

    @Audited(value = "TEST_METHOD", whenNotAuthenticated = Audited.MissingAuthPolicy.WRITE_INCOMPLETE_ENTRY)
    void testMethod() {}

}
