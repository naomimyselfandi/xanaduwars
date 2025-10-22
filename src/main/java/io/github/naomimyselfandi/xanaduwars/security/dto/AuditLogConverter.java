package io.github.naomimyselfandi.xanaduwars.security.dto;

import io.github.naomimyselfandi.xanaduwars.security.entity.AuditLog;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
interface AuditLogConverter extends Converter<AuditLog, AuditLogDto> {}
