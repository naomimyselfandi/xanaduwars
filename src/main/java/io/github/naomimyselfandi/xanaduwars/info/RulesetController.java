package io.github.naomimyselfandi.xanaduwars.info;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.auth.service.CurrentAccountService;
import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Version;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto.RulesetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/// A controller for reading information about rulesets.
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/ruleset")
public class RulesetController {

    private final RulesetService rulesetService;
    private final CurrentAccountService currentAccountService;

    @GetMapping("/{version}")
    public ResponseEntity<RulesetDto> get(@PathVariable Version version) {
        if (version.isPublished() || isDeveloper()) {
            var ruleset = rulesetService.load(version);
            if (ruleset.isPresent()) {
                return ResponseEntity.ok(new RulesetDto(ruleset.get()));
            }
        }
        return ResponseEntity.notFound().build();
    }

    private boolean isDeveloper() {
        return currentAccountService.tryGet().filter(Account::developer).isPresent();
    }

}
