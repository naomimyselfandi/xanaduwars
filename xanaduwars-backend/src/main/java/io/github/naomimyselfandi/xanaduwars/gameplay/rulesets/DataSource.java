package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;

import java.io.IOException;
import java.util.SortedSet;

interface DataSource {

    SortedSet<Version> scan();

    RulesetData load(Version version) throws IOException;

}
