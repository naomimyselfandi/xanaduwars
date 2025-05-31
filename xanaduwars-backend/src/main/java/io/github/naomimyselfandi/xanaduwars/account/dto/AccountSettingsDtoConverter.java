package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.AccountSettings;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
public interface AccountSettingsDtoConverter extends Converter<AccountSettings, AccountSettingsDto> {}
