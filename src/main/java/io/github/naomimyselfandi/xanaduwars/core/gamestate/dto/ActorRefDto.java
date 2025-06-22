package io.github.naomimyselfandi.xanaduwars.core.gamestate.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/// A DTO representing an action's actor.
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@Type(PhysicalRefDto.class), @Type(PlayerRefDto.class)})
public sealed interface ActorRefDto permits PhysicalRefDto, PlayerRefDto {}
