package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import io.github.classgraph.ClassGraph;
import io.github.naomimyselfandi.xanaduwars.core.message.Message;
import io.github.naomimyselfandi.xanaduwars.core.messages.SimpleMessageType;
import io.github.naomimyselfandi.xanaduwars.core.model.*;
import io.github.naomimyselfandi.xanaduwars.core.message.Rule;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
@Validated
@RequiredArgsConstructor
class VersionLoaderImpl implements VersionLoader {

    private final ObjectMapper objectMapper;

    @Override
    public @Valid VersionImpl load(VersionNumber versionNumber, String json) throws IOException {
        var version = new VersionImpl().setVersionNumber(versionNumber);
        var injectableValues = new InjectableValues.Std(Map.of(Version.class.getCanonicalName(), version));
        var mapper = objectMapper.copy().setInjectableValues(injectableValues);
        var attempt = mapper.readValue(json, Attempt.class);
        mapper = mapper.copy(); // not sure why this is necessary, but it doesn't work otherwise
        mapper.registerModule(new DeclarationModule(version));
        attempt.load(mapper);
        return version;
    }

    @Setter
    private static class Attempt {

        private final List<Iterable<JsonNode>> sources = new ArrayList<>();
        private final Map<UnitType, AbilityDeclaration> buildAbilities = new HashMap<>();

        @JacksonInject("io.github.naomimyselfandi.xanaduwars.core.model.Version")
        private VersionImpl version;

        private List<CustomEventType> eventTypes = List.of();
        private List<CustomQueryType> queryTypes = List.of();
        private Map<String, Script> libraries;
        private List<AbilityTag> abilityTags = List.of();
        private List<TileTag> tileTags = List.of();
        private List<UnitTag> unitTags = List.of();
        private ArrayNode globalRules;
        private ArrayNode actions;
        private ArrayNode spells;
        private ArrayNode commanders;
        private ArrayNode tileTypes;
        private ArrayNode unitTypes;
        private Target<?, ?> buildTarget;
        private Script buildFilter, buildEffect;
        private JsonNode moveAbility, fireAbility, dropAbility;
        private Script redactionPolicy;

        void load(ObjectMapper objectMapper) throws IOException {
            var messages = "io.github.naomimyselfandi.xanaduwars.core.messages";
            try (var scan = new ClassGraph().acceptPackages(messages).enableClassInfo().scan()) {
                scan.getClassesImplementing(Message.class)
                        .loadClasses()
                        .stream()
                        .filter(Class::isRecord)
                        .map(SimpleMessageType::new)
                        .forEach(it -> version.accept(it.name(), it));
            }
            for (var eventType : eventTypes) {
                version.accept(eventType.name(), eventType);
            }
            for (var queryType : queryTypes) {
                version.accept(queryType.name(), queryType);
            }
            libraries.forEach(version);
            for (var abilityTag : abilityTags) {
                version.accept(abilityTag.name(), abilityTag);
            }
            for (var tileTag : tileTags) {
                version.accept(tileTag.name(), tileTag);
            }
            for (var unitTag : unitTags) {
                version.accept(unitTag.name(), unitTag);
            }
            version.setMoveAbility(initializeAbility(moveAbility, objectMapper.readerFor(MovementAbility.class)));
            version.setFireAbility(initializeAbility(fireAbility, objectMapper.readerFor(AbilityDeclaration.class)));
            version.setDropAbility(initializeAbility(dropAbility, objectMapper.readerFor(AbilityDeclaration.class)));
            initialize(actions, AbilityDeclaration::new);
            initialize(spells, () -> new AbilityDeclaration().setSpellChoice(true));
            initialize(commanders, CommanderDeclaration::new);
            initialize(tileTypes, TileTypeDeclaration::new);
            initializeUnitTypes();
            initializeRules(objectMapper);
            for (var source : sources) {
                for (var node : source) {
                    var name = node.get("name").asText();
                    var global = version.lookup(name);
                    objectMapper.readerForUpdating(global).readValue(node);
                    if (global instanceof Commander commander) {
                        for (var spell : commander.getSignatureSpells()) {
                            ((AbilityDeclaration) spell).setSpellChoice(false);
                        }
                    }
                }
            }
            for (var entry : buildAbilities.entrySet()) {
                var unitType = entry.getKey();
                var buildAbility = entry.getValue();
                buildAbility.setSupplyCost(Script.of(unitType.getSupplyCost()));
                buildAbility.setAetherCost(Script.of(unitType.getAetherCost()));
            }
            version.setRedactionPolicy(redactionPolicy);
        }

        private <T extends AbstractSpecification> void initialize(Iterable<JsonNode> source, Supplier<T> factory) {
            for (var node : source) {
                var name = node.get("name").asText();
                var value = factory.get().setName(name);
                version.accept(name, value);
            }
            sources.add(source);
        }

        private Ability initializeAbility(JsonNode source, ObjectReader reader) throws IOException {
            var ability = (Ability) reader.readValue(source);
            version.accept(ability.getName(), ability);
            return ability;
        }

        private void initializeUnitTypes() {
            for (var node : unitTypes) {
                var name = node.get("name").asText();
                var unitType = new UnitTypeDeclaration();
                unitType.setName(name);
                version.accept(name,unitType);
                if (!BooleanNode.TRUE.equals(node.get("omitBuildAbility"))) {
                    var buildAbility = new BuildAbilityDeclaration();
                    var buildAbilityName = "Build" + name;
                    buildAbility
                            .setUnitType(unitType)
                            .setTarget(buildTarget)
                            .setFilter(buildFilter)
                            .setEffect(buildEffect)
                            .setName(buildAbilityName);
                    version.accept(buildAbilityName, buildAbility);
                    buildAbilities.put(unitType, buildAbility);
                }
            }
            sources.add(unitTypes);
        }

        private void initializeRules(ObjectMapper objectMapper) throws IOException {
            var rules = new ArrayList<Rule>(globalRules.size());
            for (var element : globalRules) {
                rules.add(objectMapper.readerFor(Rule.class).readValue(element));
            }
            version.setGlobalRules(rules);
        }

    }

}
