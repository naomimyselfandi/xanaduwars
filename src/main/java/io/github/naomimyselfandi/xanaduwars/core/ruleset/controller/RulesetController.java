package io.github.naomimyselfandi.xanaduwars.core.ruleset.controller;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.RulesetService;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/// A controller for reading information about rulesets.
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/ruleset")
public class RulesetController {

    private final VersionService versionService;
    private final RulesetService rulesetService;

    @GetMapping("/{version}")
    @PreAuthorize("hasRole('DEVELOPER') || #version.isPublished()")
    public ResponseEntity<Ruleset> get(@PathVariable Version version) {
        if (versionService.all().contains(version)) {
            var ruleset = rulesetService.load(version);
            return ResponseEntity.ok(ruleset);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
