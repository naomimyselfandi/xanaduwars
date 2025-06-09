package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

class RulesetDeserializer extends StdDeserializer<Ruleset> {

    RulesetDeserializer() {
        super(Ruleset.class);
    }

    @Override
    public Ruleset deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        var tree = parser.<JsonNode>readValueAsTree();
        var objectMapper = new ObjectMapper();
        var ruleset = init(objectMapper, tree);
        objectMapper.registerModule(new ResolverModule(ruleset));
        objectMapper.readerForUpdating(ruleset).readValue(tree);
        expand(objectMapper, tree.get(RulesetImpl.Fields.commanders), ruleset.commanders());
        expand(objectMapper, tree.get(RulesetImpl.Fields.spells), ruleset.spells());
        expand(objectMapper, tree.get(RulesetImpl.Fields.structureTypes), ruleset.structureTypes());
        expand(objectMapper, tree.get(RulesetImpl.Fields.tileTypes), ruleset.tileTypes());
        expand(objectMapper, tree.get(RulesetImpl.Fields.unitTypes), ruleset.unitTypes());
        objectMapper.readerForUpdating(ruleset).readValue(tree);
        return ruleset;
    }

    private static RulesetImpl init(ObjectMapper objectMapper, JsonNode tree) throws IOException {
        var stubs = objectMapper.readerFor(Stubs.class).<Stubs>readValue(tree);
        return new RulesetImpl()
                .commanders(init(stubs.commanders, untagged(CommanderImpl::new), CommanderId::new))
                .spells(init(stubs.spells, SpellImpl::new, SpellId::new))
                .structureTypes(init(stubs.structureTypes, StructureTypeImpl::new, StructureTypeId::new))
                .tileTypes(init(stubs.tileTypes, TileTypeImpl::new, TileTypeId::new))
                .unitTypes(init(stubs.unitTypes, UnitTypeImpl::new, UnitTypeId::new));
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

        @JsonProperty
        private Name name;

        @JsonProperty
        private Set<T> tags = Set.of();

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Stubs {

        @JsonProperty
        private List<Stub<Tag>> commanders = List.of();

        @JsonProperty
        private List<Stub<SpellTag>> spells = List.of();

        @JsonProperty
        private List<Stub<StructureTag>> structureTypes = List.of();

        @JsonProperty
        private List<Stub<TileTag>> tileTypes = List.of();

        @JsonProperty
        private List<Stub<UnitTag>> unitTypes = List.of();

    }

    private interface ElementFactory<I, T, E> {
        E create(I id, Name name, Set<T> tags);
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
