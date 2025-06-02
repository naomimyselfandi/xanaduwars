package io.github.naomimyselfandi.xanaduwars.testing;

import io.github.naomimyselfandi.seededrandom.SeededRandom;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.value.*;

import java.util.List;

/// An extension of [SeededRandom] that's aware of our domain types.
public class SeededRng extends SeededRandom {

    @SuppressWarnings("SpellCheckingInspection")
    private static final List<String> ALPHABET = List.of("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""));

    public SeededRng(long initialSeed) {
        super(initialSeed);
    }

    public AccountId nextAccountId() {
        return new AccountId(nextUUID());
    }

    public int nextIntNotNegative() {
        return nextInt(-1, Integer.MAX_VALUE) + 1;
    }

    public Version nextVersion() {
        return nextVersion(nextBoolean());
    }

    public Version nextPublishedVersion() {
        return nextVersion(false);
    }

    public Version nextInternalVersion() {
        return nextVersion(true);
    }

    private Version nextVersion(boolean internal) {
        var major = nextInt(256);
        var minor = nextInt(256);
        var patch = nextInt(256);
        var suffix = internal ? "-" + nextString() : "";
        return new Version("%d.%d.%d%s".formatted(major, minor, patch, suffix));
    }

    public Name nextName() {
        return nextName(pick(ALPHABET));
    }

    public Name nextName(String prefix) {
        return new Name(prefix + nextString());
    }

    public Tag nextTag() {
        return nextTag(pick(ALPHABET));
    }

    public Tag nextTag(String prefix) {
        return new Tag(prefix + nextString());
    }

    public Percent nextPercent() {
        return Percent.withDoubleValue(nextDouble());
    }

    public Scalar nextScalar() {
        double base = nextInt();
        return Scalar.withDoubleValue(base + nextDouble());
    }

    public Range nextRange() {
        var min = nextInt(Short.MAX_VALUE);
        var max = nextInt(min + 1, Integer.MAX_VALUE);
        return new Range(min, max);
    }

    public UnitId nextUnitId() {
        return new UnitId(nextIntNotNegative());
    }

    public TileId nextTileId() {
        return new TileId(nextIntNotNegative(), nextIntNotNegative());
    }

    public NodeId nextNodeId() {
        return nextBoolean() ? nextUnitId() : nextTileId();
    }

    public PlayerId nextPlayerId() {
        return new PlayerId(nextIntNotNegative());
    }

    public SpellId nextSpellId() {
        return new SpellId(nextPlayerId(), nextIntNotNegative());
    }

    public CommanderId nextCommanderId() {
        return new CommanderId(nextIntNotNegative());
    }

    public SpellTypeId nextSpellTypeId() {
        return new SpellTypeId(nextIntNotNegative());
    }

    public TileTypeId nextTileTypeId() {
        return new TileTypeId(nextIntNotNegative());
    }

    public UnitTypeId nextUnitTypeId() {
        return new UnitTypeId(nextIntNotNegative());
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

    private String nextString() {
        return nextUUID().toString().replaceAll("-", "");
    }

}
