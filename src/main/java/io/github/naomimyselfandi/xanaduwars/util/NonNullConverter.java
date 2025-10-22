package io.github.naomimyselfandi.xanaduwars.util;

import org.springframework.core.convert.converter.Converter;

/// A converter which never returns `null`.
public interface NonNullConverter<S, T> extends Converter<S, T> {

    @Override
    T convert(S source);

}
