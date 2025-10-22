package io.github.naomimyselfandi.xanaduwars.e2e;

import io.github.naomimyselfandi.xanaduwars.security.Audited;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SuppressWarnings("unused")
@RequestMapping("/audit/testHelper")
class AuditControllerE2EHelper {

    @Audited("PING")
    @GetMapping("/ping")
    public ResponseEntity<String> ping(@RequestBody String ping) {
        return ResponseEntity.ok(ping);
    }

}
