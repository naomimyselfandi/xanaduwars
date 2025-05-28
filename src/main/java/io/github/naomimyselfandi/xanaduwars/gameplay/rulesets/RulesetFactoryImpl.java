package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.naomimyselfandi.xanaduwars.gameplay.query.Rule;
import io.github.naomimyselfandi.xanaduwars.gameplay.Ruleset;
import io.github.naomimyselfandi.xanaduwars.gameplay.SpellType;
import io.github.naomimyselfandi.xanaduwars.gameplay.Type;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.rules.GameRule;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types.CommanderImpl;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types.SpellTypeImpl;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types.TileTypeImpl;
import io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.types.UnitTypeImpl;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
class RulesetFactoryImpl implements RulesetFactory {

    private static final TypeReference<List<GameRule<?, ?>>> RULES = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    @Valid
    @Override
    public Ruleset load(Version version, RulesetData data) throws IOException {
        log.info("Creating ruleset for version {}.", version);
        var manifest = data.manifest();
        var content = data.content();
        var ruleset = new RulesetImpl().version(version);
        ruleset.commanders(create(manifest.commanders(), CommanderId::new, CommanderImpl::new));
        ruleset.tileTypes(create(manifest.tileTypes(), TileTypeId::new, TileTypeImpl::new));
        ruleset.unitTypes(create(manifest.unitTypes(), UnitTypeId::new, UnitTypeImpl::new));
        var objectMapper = augmentObjectMapper(ruleset);
        inflate(objectMapper, ruleset.unitTypes(), content);
        inflate(objectMapper, ruleset.tileTypes(), content);
        ruleset.spellTypes(spellTypes(objectMapper, manifest.spellTypes(), content));
        inflate(objectMapper, ruleset.commanders(), content);
        ruleset.globalRules(globalRules(objectMapper, manifest.globalRules(), content));
        ruleset.details(objectMapper.readValue(content.get(Manifest.DETAILS), RulesetDetailsImpl.class));
        return ruleset;
    }

    private static <T, I> List<T> create(
            List<String> names,
            IntFunction<I> idFactory,
            BiFunction<I, Name, T> constructor
    ) {
        IntFunction<T> mapper = index -> constructor.apply(idFactory.apply(index), new Name(names.get(index)));
        return IntStream.range(0, names.size()).mapToObj(mapper).toList();
    }

    private ObjectMapper augmentObjectMapper(RulesetImpl ruleset) {
        var injector = new InjectableValues.Std(Map.of("ruleset", ruleset));
        return objectMapper.copy().registerModule(new RulesetModule(ruleset)).setInjectableValues(injector);
    }

    private static void inflate(
            ObjectMapper objectMapper,
            List<? extends Type> types,
            Map<String, String> content
    ) throws IOException {
        for (var type : types) {
            objectMapper.readerForUpdating(type).readValue(content.get(type.name().text()));
        }
    }

    private static List<SpellType<?>> spellTypes(
            ObjectMapper objectMapper,
            List<String> names,
            Map<String, String> content
    ) throws IOException {
        var result = new ArrayList<SpellTypeImpl<?>>(names.size());
        for (var name : names) result.add(objectMapper.readValue(content.get(name), SpellTypeImpl.class));
        SpellTypeImpl.initialize(result, names);
        return List.copyOf(result);
    }

    private static List<Rule<?, ?>> globalRules(
            ObjectMapper objectMapper,
            List<String> names,
            Map<String, String> content
    ) throws IOException {
        var result = new ArrayList<Rule<?, ?>>(names.size() * 3);
        for (var name : names) result.addAll(objectMapper.readValue(content.get(name), RULES));
        return List.copyOf(result);
    }

}
