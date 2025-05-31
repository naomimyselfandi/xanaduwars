package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import java.util.Map;

record RulesetData(Manifest manifest, Map<String, String> content) {

    RulesetData(Manifest manifest, Map<String, String> content) {
        this.manifest = manifest;
        this.content = Map.copyOf(content);
    }

}
