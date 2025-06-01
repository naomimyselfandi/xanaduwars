package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.controller;

import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto.RulesetDtoMapper;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto.RulesetDto;
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
    private final RulesetDtoMapper rulesetDtoMapper;

    @GetMapping("/{version}")
    @PreAuthorize("hasRole('DEVELOPER') || #version.isPublished()")
    public ResponseEntity<RulesetDto> get(@PathVariable Version version) {
        return rulesetService
                .load(version)
                .map(rulesetDtoMapper::convert)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
