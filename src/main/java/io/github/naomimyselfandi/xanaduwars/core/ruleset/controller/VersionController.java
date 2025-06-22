package io.github.naomimyselfandi.xanaduwars.core.ruleset.controller;

import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.VersionService;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/// A controller for reading information about versions.
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/version")
public class VersionController {

    private final VersionService versionService;

    @GetMapping
    public ResponseEntity<List<Version>> get(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(versions(pageable, versionService.published()).toList());
    }

    @GetMapping("/internal")
    @PreAuthorize("hasRole('ROLE_DEVELOPER')")
    public ResponseEntity<List<Version>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(versions(pageable, versionService.all()).toList());
    }

    private Stream<Version> versions(Pageable pageable, Collection<Version> list) {
        return list.stream().skip(pageable.getOffset()).limit(pageable.getPageSize());
    }

}
