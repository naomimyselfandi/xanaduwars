package io.github.naomimyselfandi.xanaduwars.security.controller;

import io.github.naomimyselfandi.xanaduwars.security.dto.AuditLogDto;
import io.github.naomimyselfandi.xanaduwars.security.dto.AuditLogFilterDto;
import io.github.naomimyselfandi.xanaduwars.security.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// The main controller for working with audit logs.
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/audit")
 @PreAuthorize("hasRole('MODERATOR')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<Page<AuditLogDto>> get(
            @ModelAttribute AuditLogFilterDto filter,
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(auditService.find(filter, pageable));
    }

}
