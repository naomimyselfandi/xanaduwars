package io.github.naomimyselfandi.xanaduwars.core.runtime;

import io.github.naomimyselfandi.xanaduwars.core.GameState;
import io.github.naomimyselfandi.xanaduwars.core.Ruleset;
import io.github.naomimyselfandi.xanaduwars.core.VersionService;
import io.github.naomimyselfandi.xanaduwars.core.data.GameData;
import io.github.naomimyselfandi.xanaduwars.core.data.GameDataFactory;
import io.github.naomimyselfandi.xanaduwars.core.QueryEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
final class GameStateFactoryImpl implements GameStateFactory {

    private final Map<Ruleset, QueryEvaluator> cache = new ConcurrentHashMap<>();

    private final VersionService versionService;
    private final GameDataFactory gameDataFactory;
    private final ActionPolicy actionPolicy;
    private final ActionExecutor actionExecutor;

    @Override
    public GameState create(GameData gameData) {
        var ruleset = versionService.load(gameData.versionNumber());
        var evaluator = cache.computeIfAbsent(ruleset, QueryEvaluatorImpl::new);
        return new GameStateImpl(gameData, ruleset, gameDataFactory, evaluator, actionPolicy, actionExecutor);
    }

}
