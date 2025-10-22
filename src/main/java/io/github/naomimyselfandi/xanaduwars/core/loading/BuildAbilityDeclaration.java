package io.github.naomimyselfandi.xanaduwars.core.loading;

import io.github.naomimyselfandi.xanaduwars.core.model.Actor;
import io.github.naomimyselfandi.xanaduwars.core.model.UnitType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
final class BuildAbilityDeclaration extends AbilityDeclaration {

    private UnitType unitType;

    @Override
    Map<String, Object> makeArgumentMap(Actor actor, Object target) {
        var base = super.makeArgumentMap(actor, target);
        var result = new HashMap<String, Object>(base.size() + 1);
        result.putAll(base);
        result.put("unitType", unitType);
        return result;
    }

}
