package io.github.naomimyselfandi.xanaduwars.core.gamestate.internal;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.CopyMachine;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.service.GameStateFactory;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.RulesetService;
import io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator.QueryEvaluatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class GameStateFactoryImpl implements GameStateFactory {

    private final Redactor redactor;
    private final CopyMachine copyMachine;
    private final RulesetService rulesetService;
    private final SpellSlotHelper spellSlotHelper;
    private final QueryEvaluatorFactory queryEvaluatorFactory;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public GameStateImpl create(GameStateData gameStateData) {
        var ruleset = rulesetService.load(gameStateData.getVersion());
        var queryEvaluator = queryEvaluatorFactory.create(ruleset);
        queryEvaluator = new QueryEvaluatorWithCleaner(queryEvaluator, new CleanerForMemory());
        queryEvaluator = new QueryEvaluatorWithCleaner(queryEvaluator, new CleanerForTurn());
        return new GameStateImpl(ruleset, gameStateData, queryEvaluator, copyMachine, redactor, spellSlotHelper);
    }

}
