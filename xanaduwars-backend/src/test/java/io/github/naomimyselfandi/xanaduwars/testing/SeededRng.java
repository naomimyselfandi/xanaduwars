package io.github.naomimyselfandi.xanaduwars.testing;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.core.gameplay.entity.*;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.model.Version;
import jakarta.persistence.Entity;
import lombok.SneakyThrows;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static org.mockito.Mockito.mock;

/// An extension of [SeededRandom] that's aware of our domain types.
public class SeededRng extends SeededRandom {

    public SeededRng(long initialSeed) {
        super(initialSeed);
    }

    public <T> T get(Class<T> type) {
        @SuppressWarnings("unchecked")
        var result = (T) get0(type);
        return result;
    }

    public Object get(ParameterizedType parameterizedType) {
        var rawType = parameterizedType.getRawType();
        var arguments = parameterizedType.getActualTypeArguments();
        if (rawType == List.class || rawType == Collection.class || rawType == Iterable.class) {
            return List.of(get(arguments[0]), get(arguments[0]), get(arguments[0]));
        } else if (rawType == Set.class) {
            return Set.of(get(arguments[0]), get(arguments[0]), get(arguments[0]));
        } else if (rawType == Map.class) {
            return Map.of(get(arguments[0]), get(arguments[1]));
        } else if (rawType == Optional.class) {
            return Optional.ofNullable(nextBoolean() ? get(arguments[0]) : null);
        } else {
            return get(rawType);
        }
    }

    public Object get(Type type) {
        return switch (type) {
            case Class<?> c -> get(c);
            case ParameterizedType p -> get(p);
            default -> throw new IllegalStateException(type.getTypeName());
        };
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> T get(T... exclude) {
        T result;
        do {
            result = (T) get(exclude.getClass().componentType());
        } while (Arrays.asList(exclude).contains(result));
        return result;
    }

    public int nextIntNotNegative() {
        return nextInt(-1, Integer.MAX_VALUE) + 1;
    }

    public String nextString() {
        return nextUUID().toString().replaceAll("-", "");
    }

    public PlayerData nextPlayerData() {
        return new PlayerData()
                .setId(get())
                .setTeam(get())
                .setCommander(get())
                .setSpellSlots(new SpellSlotList(List.of(get(), get(), get())))
                .setSupplies(nextIntNotNegative())
                .setAether(nextIntNotNegative())
                .setFocus(nextIntNotNegative());
    }

    public StructureData nextStructureData() {
        return new StructureData()
                .setType(get())
                .setOwner(get())
                .setHp(nextInt(0, 101))
                .setComplete(nextBoolean());
    }

    public TileData nextTileData() {
        return new TileData()
                .setId(get())
                .setType(get())
                .setStructureData(nextBoolean() ? nextStructureData() : null)
                .setMemory(new Memory(Map.of(get(), get())));
    }

    public UnitData nextUnitData() {
        return new UnitData()
                .setId(get())
                .setType(get())
                .setOwner(get())
                .setHp(nextInt(0, 101))
                .setLocation(get());
    }

    public AccountId nextAccountId() {
        return new AccountId(nextUUID());
    }

    public Username nextUsername() {
        return new Username(nextString().toUpperCase());
    }

    public Password nextPassword() {
        return new Password(nextString());
    }

    public PlaintextPassword nextPlaintextPassword() {
        return new PlaintextPassword(nextString());
    }

    public APIKey nextAPIKey() {
        return new APIKey(nextString());
    }

    public PlaintextAPIKey nextPlaintextAPIKey() {
        return new PlaintextAPIKey(nextString());
    }

    public EmailAddress nextEmailAddress() {
        return new EmailAddress("%s@%s.%s".formatted(nextUUID(), nextUUID(), nextUsername()));
    }

    public Version nextVersion() {
        var major = nextInt(256);
        var minor = nextInt(256);
        var patch = nextInt(256);
        var suffix = nextBoolean() ? "-" + nextString() : "";
        return new Version("%d.%d.%d%s".formatted(major, minor, patch, suffix));
    }

    public History nextHistory() {
        return new History(List.of(get(), get(), get()));
    }

    @SneakyThrows
    private Object get0(Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return nextIntNotNegative();
        } else if (type == double.class || type == Double.class) {
            return nextDouble();
        } else if (type == boolean.class || type == Boolean.class) {
            return nextBoolean();
        } else if (hasMethod(type)) {
            return getClass().getMethod("next" + type.getSimpleName()).invoke(this);
        } else if (type.isEnum()) {
            return pick(type.getEnumConstants());
        } else if (type.isSealed()) {
            return get0(pick(type.getPermittedSubclasses()));
        } else if (type.isInterface()) {
            return mock(type);
        } else if (type.isRecord()) {
            var constructor = pick(type.getDeclaredConstructors());
            constructor.setAccessible(true);
            var values = Arrays.stream(constructor.getGenericParameterTypes()).map(this::get).toArray();
            return constructor.newInstance(values);
        } else if (type.isAnnotationPresent(Entity.class)) {
            return new ObjectMapper().readValue("{\"id\": \"%s\"}".formatted(nextUUID()), type);
        } else {
            return new ObjectMapper().readValue("{}", type);
        }
    }

    private boolean hasMethod(Class<?> type) {
        try {
            var method = getClass().getMethod("next" + type.getSimpleName());
            return type.isAssignableFrom(method.getReturnType());
        } catch (NoSuchMethodException _) {
            return false;
        }
    }

}
