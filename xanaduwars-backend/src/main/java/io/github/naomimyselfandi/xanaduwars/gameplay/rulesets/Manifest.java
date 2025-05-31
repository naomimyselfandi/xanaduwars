package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import lombok.experimental.FieldNameConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FieldNameConstants(asEnum = true)
record Manifest(
        List<String> globalRules,
        List<String> commanders,
        List<String> spellTypes,
        List<String> tileTypes,
        List<String> unitTypes
) {

    static final String DETAILS = "details";

    Set<String> toSet() {
        var declared = Arrays.stream(Fields.values()).map(field -> switch (field) {
            case globalRules -> globalRules();
            case commanders -> commanders();
            case spellTypes -> spellTypes();
            case tileTypes -> tileTypes();
            case unitTypes -> unitTypes();
        }).flatMap(List::stream);
        return Stream.concat(declared, Stream.of(DETAILS)).collect(Collectors.toSet());
    }

}
