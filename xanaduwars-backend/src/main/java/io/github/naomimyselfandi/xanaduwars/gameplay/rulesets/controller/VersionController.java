package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.controller;

import io.github.naomimyselfandi.xanaduwars.gameplay.VersionService;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/// A controller for reading information about versions.
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/version")
public class VersionController {

    private final VersionService versionService;

    @GetMapping
    public ResponseEntity<List<Version>> get(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(versions(pageable, Version::isPublished).toList());
    }

    @GetMapping("/internal")
    @PreAuthorize("hasRole('ROLE_DEVELOPER')")
    public ResponseEntity<List<Version>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(versions(pageable, _ -> true).toList());
    }

    private Stream<Version> versions(Pageable pageable, Predicate<Version> predicate) {
        return versionService.stream().filter(predicate).skip(pageable.getOffset()).limit(pageable.getPageSize());
    }

}
