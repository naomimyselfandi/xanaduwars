package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
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
            case Player player -> actions(ruleset.details(), player);
            case Spell _ -> List.of();
            case Tile tile -> actions(ruleset.details(), tile);
            case Unit unit -> actions(ruleset.details(), unit);
        };
        return result;
    }

    private static List<Action<Player, ?>> actions(Ruleset.Details details, Player player) {
        var globalActions = Stream.of(details.passAction(), details.resignAction());
        return Stream.concat(player.knownSpells().stream(), globalActions).toList();
    }

    private static List<Action<Tile, ?>> actions(Ruleset.Details details, Tile tile) {
        return tile.deploymentRoster().isEmpty() ? List.of() : List.of(details.deployAction());
    }

    private static List<Action<Unit, ?>> actions(Ruleset.Details details, Unit unit) {
        var abilities = unit.abilities();
        var result = new ArrayList<Action<Unit, ?>>(abilities.size() + 4);
        if (unit.speed() > 0) {
            result.add(details.moveAction());
        }
        if (!unit.damageTable().isEmpty()) {
            result.add(details.attackAction());
        }
        if (!unit.cargo().isEmpty()) {
            result.add(details.dropAction());
        }
        result.addAll(abilities);
        result.add(details.waitAction());
        return result;
    }

}
