package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/// A sequence of commands.
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@Type(CommandSequenceForSelf.class), @Type(CommandSequenceForUnit.class)})
public interface CommandSequence {

    /// Submit this command sequence to some game state.
    boolean submit(GameState gameState) throws CommandException;

}
