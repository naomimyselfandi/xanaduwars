package io.github.naomimyselfandi.xanaduwars.account.dto;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

@Mapper
interface BaseAccountDtoConverter extends Converter<Account, BaseAccountDto> {

    String LAST_SEEN_AT = "source.getSettings().isHideActivity() ? null : source.getLastSeenAt()";

    @Override
    @Mapping(target = "lastSeenAt", expression = "java(" + LAST_SEEN_AT + ")")
    BaseAccountDto convert(Account source);

}
