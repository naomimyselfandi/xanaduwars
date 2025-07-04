package io.github.naomimyselfandi.xanaduwars.audit.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.audit.dto.AuditLogDto;
import io.github.naomimyselfandi.xanaduwars.audit.dto.AuditLogFilterDto;
import io.github.naomimyselfandi.xanaduwars.audit.service.AuditService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuditControllerTest {

    @Mock
    private Page<AuditLogDto> page;

    @Mock
    private Pageable pageable;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController fixture;

    @Test
    void get(SeededRng random) {
        var filter = random.<AuditLogFilterDto>get();
        when(auditService.find(filter, pageable)).thenReturn(page);
        assertThat(fixture.get(filter, pageable)).isEqualTo(ResponseEntity.ok(page));
    }

}
