package io.github.naomimyselfandi.xanaduwars.account.service;

import io.github.naomimyselfandi.xanaduwars.account.value.AccountId;
import io.github.naomimyselfandi.xanaduwars.account.value.AccountReference;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class AccountReferenceConverter implements Converter<String, AccountReference> {

    @Override
    public AccountReference convert(String source) {
        if ("me".equalsIgnoreCase(source)) {
            return AccountReference.ME;
        } else try {
            return new AccountId(UUID.fromString(source));
        } catch (IllegalArgumentException e) {
            var sourceType = TypeDescriptor.valueOf(String.class);
            var targetType = TypeDescriptor.valueOf(AccountReference.class);
            throw new ConversionFailedException(sourceType, targetType, source, e);
        }
    }

}
