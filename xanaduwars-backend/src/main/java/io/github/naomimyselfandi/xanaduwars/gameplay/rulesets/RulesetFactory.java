package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;

import java.io.IOException;

interface RulesetFactory {

    Ruleset load(Version version, RulesetData data) throws IOException;

}
