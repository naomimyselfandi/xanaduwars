package io.github.naomimyselfandi.xanaduwars.core.service;

import io.github.naomimyselfandi.xanaduwars.core.messages.AreSpellChoicesOkQuery;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
class SpellChoiceServiceImpl implements SpellChoiceService {

    @Override
    public void select(Player player, Commander commander, List<Ability> abilities) throws SpellChoiceException {
        if (player.getGameState().evaluate(new AreSpellChoicesOkQuery(commander, abilities))) {
            player.setCommander(commander);
            player.setAbilities(Stream.concat(commander.getSignatureSpells().stream(), abilities.stream()).toList());
        } else {
            throw new SpellChoiceException("Illegal spell choices for '%s'.".formatted(commander.getName()));
        }
    }

    @Override
    public List<Ability> getSuggestions(GameState gameState, Commander commander, List<Ability> abilities) {
        return gameState
                .getVersion()
                .getDeclarations()
                .flatMap(it -> Stream.ofNullable(it instanceof Ability ability ? ability : null))
                .filter(candidate -> {
                    var augmented = Stream.concat(abilities.stream(), Stream.of(candidate)).toList();
                    return gameState.evaluate(new AreSpellChoicesOkQuery(commander, augmented));
                })
                .toList();
    }

}
