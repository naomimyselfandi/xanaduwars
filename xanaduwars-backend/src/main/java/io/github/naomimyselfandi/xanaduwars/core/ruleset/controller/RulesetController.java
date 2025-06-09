package io.github.naomimyselfandi.xanaduwars.core.ruleset.controller;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.RulesetService;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/// A controller for reading information about rulesets.
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/ruleset")
public class RulesetController {

    private final RulesetService rulesetService;

    @GetMapping("/{version}")
    @PreAuthorize("hasRole('DEVELOPER') || #version.isPublished()")
    public ResponseEntity<Ruleset> get(@PathVariable Version version) {
        var ruleset = rulesetService.load(version);
        return ruleset != null ? ResponseEntity.ok(ruleset) : ResponseEntity.notFound().build();
    }

}
