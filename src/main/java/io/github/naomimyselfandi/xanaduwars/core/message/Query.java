package io.github.naomimyselfandi.xanaduwars.core.message;

import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.springframework.core.convert.TypeDescriptor;

public interface Query<T> extends Message {

    T defaultValue(ScriptRuntime runtime);

    TypeDescriptor resultType();

}
