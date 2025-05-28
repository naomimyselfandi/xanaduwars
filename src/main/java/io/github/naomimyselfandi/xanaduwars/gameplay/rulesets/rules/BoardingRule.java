package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules;

import io.github.naomimyselfandi.xanaduwars.gameplay.Unit;
import io.github.naomimyselfandi.xanaduwars.gameplay.queries.EntryQuery;

/// A rule that governs which units are allowed to board which transports.
public record BoardingRule() implements GameRule<EntryQuery, Double> {

    @Override
    public Double handle(EntryQuery query, Double value) {
        return Double.NaN;
    }

    @Override
    public boolean handles(EntryQuery query, Double value) {
        var transport = query.target().unit();
        return transport.isPresent() && handles(query.subject(), transport.get());
    }

    private boolean handles(Unit mover, Unit transport) {
        return !mover.owner().equals(transport.owner())
                || !transport.cargo().isEmpty() // Assuming a maximum hangar size of 1 for now.
                || transport.hangar().stream().noneMatch(mover.tags());
    }

}
