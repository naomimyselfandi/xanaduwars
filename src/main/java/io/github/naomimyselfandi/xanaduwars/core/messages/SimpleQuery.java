package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.xanaduwars.core.message.Query;
import org.springframework.core.convert.TypeDescriptor;

/// A query implemented as a Java record.
public interface SimpleQuery<T> extends SimpleMessage, Query<T> {

    @Override
    default TypeDescriptor resultType() {
        return resultType0();
    }

    @SuppressWarnings("DataFlowIssue")
    private TypeDescriptor resultType0() {
        return TypeDescriptor.forObject(this).upcast(Query.class).nested(1);
    }

}
