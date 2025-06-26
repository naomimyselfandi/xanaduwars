package io.github.naomimyselfandi.xanaduwars.core.gamestate.service.command;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.Element;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.queries.*;
import io.github.naomimyselfandi.xanaduwars.core.scripting.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ItemProcessorImpl implements ItemProcessor {

    @Override
    public Result process(Element actor, Command.Item item) {
        var game = actor.getGameState();
        var action = item.action();
        if (game.evaluate(new ActionUsageQuery(actor, action)) instanceof Result.Fail fail) {
            return fail;
        }
        var targets = item.targets();
        if (game.evaluate(new ActionTargetQuery(actor, action, targets)) instanceof Result.Fail fail) {
            return fail;
        }
        var player = actor.getOwner().orElseThrow();
        var supplies = player.getSupplies() - game.evaluate(new SupplyCostQuery(actor, action, targets));
        if (supplies < 0) return Result.fail("Insufficient supplies.");
        var aether = player.getAether() - game.evaluate(new AetherCostQuery(actor, action, targets));
        if (aether < 0) return Result.fail("Insufficient aether.");
        var focus = player.getFocus() - game.evaluate(new FocusCostQuery(actor, action, targets));
        if (focus < 0) return Result.fail("Insufficient focus.");
        player.setSupplies(supplies);
        player.setAether(aether);
        player.setFocus(focus);
        return game.evaluate(new ActionExecutionEvent(actor, action, targets));
    }

}
