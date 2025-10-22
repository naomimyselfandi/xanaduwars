package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.message.Query;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.springframework.core.convert.TypeDescriptor;

import java.util.HashMap;
import java.util.List;

public record CustomQuery(CustomQueryType type, List<Object> arguments) implements Query<Object> {

    private static final TypeDescriptor TYPE_DESCRIPTOR = TypeDescriptor.valueOf(Object.class);

    @Override
    public Object defaultValue(ScriptRuntime runtime) {
        var properties = type.properties();
        var count = properties.size();
        var map = new HashMap<String, Object>(count);
        for (var i = 0; i < count; i++) {
            map.put(properties.get(i), arguments.get(i));
        }
        return type.defaultValue().executeNotNull(runtime, map);
    }

    @Override
    public TypeDescriptor resultType() {
        return TYPE_DESCRIPTOR;
    }

}
