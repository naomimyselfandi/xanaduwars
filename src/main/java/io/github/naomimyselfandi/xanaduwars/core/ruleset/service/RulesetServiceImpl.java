package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.classgraph.ClassGraph;
import io.github.naomimyselfandi.xanaduwars.core.common.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Affinity;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.Ruleset;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
class RulesetServiceImpl implements RulesetService {

    @Value("${xanadu.core.ruleset.root}")
    private final String root;

    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "configCache")
    public @Valid Ruleset load(Version version) {
        try (var scan = new ClassGraph().acceptPaths(root).scan()) {
            log.info("Loading ruleset for version {}.", version);
            try (var json = scan.getResourcesWithLeafName("%s.json".formatted(version)).getFirst()) {
                var om = objectMapper.copy();
                var rulesetDeserializer = new RulesetDeserializer(version, om);
                om.registerModule(new SimpleModule().addDeserializer(Ruleset.class, rulesetDeserializer));
                return om.readValue(json.load(), Ruleset.class);
            }
        } catch (Exception e) {
            log.error("Failed loading ruleset for version {}.", version, e);
            throw new RuntimeException("Failed loading ruleset for version %s.".formatted(version), e);
        }
    }

    private static class RulesetDeserializer extends StdDeserializer<Ruleset> {

        private final Version version;
        private final ObjectMapper objectMapper;

        RulesetDeserializer(Version version, ObjectMapper objectMapper) {
            super(Ruleset.class);
            this.version = version;
            this.objectMapper = objectMapper;
        }

        @Override
        public Ruleset deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            var tree = parser.<JsonNode>readValueAsTree();
            var ruleset = init(objectMapper, tree);
            objectMapper.registerModule(new ResolverModule(ruleset.getScriptConstants()));
            objectMapper.readerForUpdating(ruleset).readValue(tree);
            expand(objectMapper, tree.get(RulesetImpl.Fields.actions), ruleset.getActions());
            expand(objectMapper, tree.get(RulesetImpl.Fields.commanders), ruleset.getCommanders());
            expand(objectMapper, tree.get(RulesetImpl.Fields.spells), ruleset.getSpells());
            expand(objectMapper, tree.get(RulesetImpl.Fields.structureTypes), ruleset.getStructureTypes());
            expand(objectMapper, tree.get(RulesetImpl.Fields.tileTypes), ruleset.getTileTypes());
            expand(objectMapper, tree.get(RulesetImpl.Fields.unitTypes), ruleset.getUnitTypes());
            objectMapper.readerForUpdating(ruleset).readValue(tree);
            for (var commander : ruleset.getCommanders()) {
                for (var spell : commander.getSignatureSpells()) {
                    ((SpellImpl) spell).setSignatureSpell(true);
                }
            }
            return ruleset;
        }

        private RulesetImpl init(ObjectMapper objectMapper, JsonNode tree) throws IOException {
            var stubs = objectMapper.readerFor(Stubs.class).<Stubs>readValue(tree);
            return new RulesetImpl()
                    .setVersion(version)
                    .setActions(init(stubs.actions, withoutId(NormalActionImpl::new), _ -> 0))
                    .setCommanders(init(stubs.commanders, untagged(CommanderImpl::new), CommanderId::new))
                    .setSpells(init(stubs.spells, SpellImpl::new, SpellId::new))
                    .setAffinities(List.copyOf(stubs.affinities))
                    .setStructureTypes(init(stubs.structureTypes, StructureTypeImpl::new, StructureTypeId::new))
                    .setTileTypes(init(stubs.tileTypes, TileTypeImpl::new, TileTypeId::new))
                    .setUnitTypes(init(stubs.unitTypes, UnitTypeImpl::new, UnitTypeId::new));
        }

        private static void expand(ObjectMapper mapper, JsonNode source, List<?> targets) throws IOException {
            var sources = source.elements();
            for (var target : targets) {
                mapper.readerForUpdating(target).readValue(sources.next());
            }
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Stub<T extends Tag> {

            private Name name;

            private Set<T> tags = Set.of();

        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Stubs {
            private List<Stub<ActionTag>> actions = List.of();
            private List<Stub<Tag>> commanders = List.of();
            private List<Stub<SpellTag>> spells = List.of();
            private List<Affinity> affinities = List.of();
            private List<Stub<StructureTag>> structureTypes = List.of();
            private List<Stub<TileTag>> tileTypes = List.of();
            private List<Stub<UnitTag>> unitTypes = List.of();
        }

        private interface ElementFactory<I, T, E> {
            E create(I id, Name name, Set<T> tags);
        }

        private static <I, T, E> ElementFactory<I, T, E> withoutId(BiFunction<Name, Set<T>, E> factory) {
            return (_, name, tags) -> factory.apply(name, tags);
        }

        private static <I, T, E> ElementFactory<I, T, E> untagged(BiFunction<I, Name, E> factory) {
            return (id, name, _) -> factory.apply(id, name);
        }

        private static <I, T extends Tag, E> List<E> init(
                List<Stub<T>> stubs,
                ElementFactory<I, T, E> elementFactory,
                IntFunction<I> idFactory
        ) {
            return IntStream
                    .range(0, stubs.size())
                    .mapToObj(i -> {
                        var stub = stubs.get(i);
                        return elementFactory.create(idFactory.apply(i), stub.name, stub.tags);
                    })
                    .toList();
        }

    }

}
