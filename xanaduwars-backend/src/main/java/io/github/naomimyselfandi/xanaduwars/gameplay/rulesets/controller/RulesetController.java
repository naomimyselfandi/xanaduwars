package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.controller;

import io.github.naomimyselfandi.xanaduwars.account.value.Role;
import io.github.naomimyselfandi.xanaduwars.auth.Authenticated;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto.RulesetDtoMapper;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto.RulesetDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/// A controller for reading information about rulesets.
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/ruleset")
public class RulesetController {

    private final RulesetService rulesetService;
    private final RulesetDtoMapper rulesetDtoMapper;

    @GetMapping("/{version}")
    public ResponseEntity<RulesetDto> get(
            @Authenticated(required = false) @Nullable UserDetailsDto user,
            @PathVariable Version version
    ) {
        if (version.isPublished() || isDeveloper(user)) {
            var ruleset = rulesetService.load(version);
            if (ruleset.isPresent()) {
                return ResponseEntity.ok(rulesetDtoMapper.convert(ruleset.get()));
            }
        }
        return ResponseEntity.notFound().build();
    }

    private boolean isDeveloper(@Nullable UserDetailsDto user) {
        return user != null && user.getAuthorities().contains(Role.DEVELOPER);
    }

}
