package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.AccountSettings;
import org.mapstruct.Mapper;
import org.mapstruct.extensions.spring.DelegatingConverter;
import org.springframework.core.convert.converter.Converter;

@Mapper
@SuppressWarnings("unused")
interface AccountSettingsDtoConverter extends Converter<AccountSettings, AccountSettingsDto> {

    @DelegatingConverter
    AccountSettings convert(AccountSettingsDto source);

}
