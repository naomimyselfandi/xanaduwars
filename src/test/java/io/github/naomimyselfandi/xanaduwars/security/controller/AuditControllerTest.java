package io.github.naomimyselfandi.xanaduwars.security.controller;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.security.dto.AuditLogDto;
import io.github.naomimyselfandi.xanaduwars.security.dto.AuditLogFilterDto;
import io.github.naomimyselfandi.xanaduwars.security.service.AuditService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class AuditControllerTest {

    @Mock
    private PagedModel<EntityModel<AuditLogDto>> pagedModel;

    @Mock
    private Page<AuditLogDto> page;

    @Mock
    private Pageable pageable;

    @Mock
    private PagedResourcesAssembler<AuditLogDto> assembler;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController fixture;

    @Test
    void get(SeededRng random) {
        var filter = random.<AuditLogFilterDto>get();
        when(auditService.find(filter, pageable)).thenReturn(page);
        when(assembler.toModel(page)).thenReturn(pagedModel);
        assertThat(fixture.get(filter, pageable, assembler)).isEqualTo(pagedModel);
    }

}
