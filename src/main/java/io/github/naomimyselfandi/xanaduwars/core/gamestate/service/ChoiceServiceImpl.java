package io.github.naomimyselfandi.xanaduwars.core.gamestate.service;

import io.github.naomimyselfandi.xanaduwars.core.common.CommanderId;
import io.github.naomimyselfandi.xanaduwars.core.common.SpellId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.GameState;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.PlayerId;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.SpellChoiceQuery;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Commander;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Spell;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import io.github.naomimyselfandi.xanaduwars.util.InvalidOperationException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class ChoiceServiceImpl implements ChoiceService {

    @Override
    public ChoiceDto getChoices(GameState gameState, PlayerId playerId) {
        var player = gameState.getPlayers().get(playerId.playerId());
        var commanderId = Optional.ofNullable(player.getCommander()).map(Commander::getId).orElse(null);
        var spellIds = player.getChosenSpells().stream().map(Spell::getId).toList();
        return new ChoiceDto().setCommander(commanderId).setSpells(spellIds);
    }

    @Override
    @SuppressWarnings("DeconstructionCanBeUsed") // It confuses the coverage report
    public void setChoices(GameState gameState, PlayerId playerId, ChoiceDto choices) throws InvalidOperationException {
        var ruleset = gameState.getRuleset();
        var player = gameState.getPlayers().get(playerId.playerId());
        var commander = getCommander(ruleset, choices.getCommander());
        var chosenSpells = getSpells(ruleset, choices.getSpells());
        if (gameState.evaluate(new SpellChoiceQuery(commander, chosenSpells)) instanceof Result.Fail fail) {
            throw new InvalidOperationException(fail.message());
        } else {
            player.setCommander(commander).setChosenSpells(chosenSpells);
        }
    }

    private Commander getCommander(Ruleset ruleset, @Nullable CommanderId commanderId)
            throws InvalidOperationException {
        if (commanderId == null) {
            throw new InvalidOperationException("Must choose a commander.");
        } else try {
            return ruleset.getCommander(commanderId);
        } catch (IndexOutOfBoundsException _) {
            throw new InvalidOperationException("Unknown commander.");
        }
    }

    private List<Spell> getSpells(Ruleset ruleset, List<SpellId> spellIds) throws InvalidOperationException {
        try {
            return spellIds.stream().map(ruleset::getSpell).toList();
        } catch (IndexOutOfBoundsException _) {
            throw new InvalidOperationException("Unknown spell.");
        }
    }

}
