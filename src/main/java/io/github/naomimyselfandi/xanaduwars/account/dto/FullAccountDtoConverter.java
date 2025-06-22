package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
interface FullAccountDtoConverter extends Converter<Account, FullAccountDto> {}
