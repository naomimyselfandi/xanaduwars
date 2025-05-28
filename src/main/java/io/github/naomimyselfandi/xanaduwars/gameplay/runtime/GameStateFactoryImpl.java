package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.GameState;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.RulesetService;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameData;
import io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data.GameDataFactory;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.QueryEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
class GameStateFactoryImpl implements GameStateFactory {

    private final Map<Ruleset, QueryEvaluator> cache = new ConcurrentHashMap<>();

    private final RulesetService rulesetService;
    private final GameDataFactory gameDataFactory;
    private final ActionPolicy actionPolicy;
    private final ActionExecutor actionExecutor;

    @Override
    public GameState create(GameData gameData) {
        var ruleset = rulesetService.load(gameData.version()).orElseThrow();
        var evaluator = cache.computeIfAbsent(ruleset, QueryEvaluatorImpl::new);
        return new GameStateImpl(gameData, ruleset, gameDataFactory, evaluator, actionPolicy, actionExecutor);
    }

}
