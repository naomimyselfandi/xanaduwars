package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
final class ActionPolicyImpl implements ActionPolicy {

    @Override
    public <S extends Element> List<Action<? super S, ?>> actions(Ruleset ruleset, S user) {
        @SuppressWarnings("unchecked")
        var result = (List<Action<? super S, ?>>) switch (user) {
            case Player player -> actions(ruleset, player);
            case Spell _ -> List.of();
            case Tile tile -> actions(ruleset, tile);
            case Unit unit -> actions(ruleset, unit);
        };
        return result;
    }

    private static List<Action<Player, ?>> actions(Ruleset ruleset, Player player) {
        var globalActions = Stream.of(ruleset.passAction(), ruleset.resignAction());
        return Stream.concat(player.knownSpells().stream(), globalActions).toList();
    }

    private static List<Action<Tile, ?>> actions(Ruleset ruleset, Tile tile) {
        return tile.deploymentRoster().isEmpty() ? List.of() : List.of(ruleset.deployAction());
    }

    private static List<Action<Unit, ?>> actions(Ruleset ruleset, Unit unit) {
        var abilities = unit.abilities();
        var result = new ArrayList<Action<Unit, ?>>(abilities.size() + 4);
        if (unit.speed() > 0) {
            result.add(ruleset.moveAction());
        }
        if (!unit.damageTable().isEmpty()) {
            result.add(ruleset.attackAction());
        }
        if (!unit.cargo().isEmpty()) {
            result.add(ruleset.dropAction());
        }
        result.addAll(abilities);
        result.add(ruleset.waitAction());
        return result;
    }

}
