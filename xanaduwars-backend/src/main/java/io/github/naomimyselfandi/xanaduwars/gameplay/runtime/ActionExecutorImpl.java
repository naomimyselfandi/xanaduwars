package io.github.naomimyselfandi.xanaduwars.gameplay.runtime;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Execution;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Resource;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionCleanupEvent;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionGroupValidation;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.ActionValidation;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.StandardTargetValidation;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Validation;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

@Service
final class ActionExecutorImpl implements ActionExecutor {

    @Override
    public <S extends Actor> void execute(List<? extends ActionItem<S, ?>> actionItems, S user) throws ActionException {
        new Attempt<>(actionItems, user).execute();
    }

    private static class Attempt<S extends Actor> implements Consumer<Rule<?, ?>> {

        private @Nullable Action<S, ?> action;
        private @Nullable Rule<?, ?> rule;

        private final List<? extends ActionItem<S, ?>> actionItems;
        private final S user;
        private final GameState gameState;
        private final Player player;

        Attempt(List<? extends ActionItem<S, ?>> actionItems, S user) {
            this.actionItems = actionItems;
            this.user = user;
            this.gameState = user.gameState();
            this.player = gameState.activePlayer();
        }

        void execute() throws ActionException {
            validateGroup();
            validateReadiness();
            try {
                for (var item : actionItems) {
                    var interrupted = switch (execute(item)) {
                        case SUCCESSFUL -> false;
                        case INTERRUPTED -> true;
                    };
                    if (interrupted) break;
                }
            } finally {
                gameState.evaluate(new ActionCleanupEvent(user));
            }
        }

        private void validateGroup() throws ActionException {
            validate(new ActionGroupValidation(actionItems.stream().map(ActionItem::action).toList()));
        }

        private void validateReadiness() throws ActionException {
            validate(new ActionValidation(user));
        }

        private <T> Execution execute(ActionItem<S, T> item) throws ActionException {
            var action = item.action();
            var target = item.target();
            this.action = action;
            validate(new StandardTargetValidation<>(action, user, target));
            var resources = new EnumMap<Resource, Integer>(Resource.class);
            resources.putAll(player.resources());
            for (var resource : Resource.values()) {
                var available = resources.getOrDefault(resource, 0);
                var cost = action.cost(resource, user, target);
                var total = available - cost;
                if (total < 0) {
                    throw new ActionException(action, resource);
                } else {
                    resources.put(resource, total);
                }
            }
            resources.forEach(player::resource);
            return action.execute(user, target);
        }

        private void validate(Validation validation) throws ActionException {
            if (!gameState.evaluate(validation, this)) throw new ActionException(action, rule);
        }

        @Override
        public void accept(Rule<?, ?> rule) {
            this.rule = rule;
        }

    }

}
