package io.github.naomimyselfandi.xanaduwars.core.wrapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class NodeIdConverter extends IntWrapperConverter<NodeId> implements AttributeConverter<NodeId, Integer> {

    NodeIdConverter() {
        super(NodeId::withIntValue);
    }

}
