package io.github.naomimyselfandi.xanaduwars.core.model;

import com.fasterxml.jackson.databind.JsonNode;

/// An instruction for an actor to use an ability.
public record Command(String abilityName, JsonNode target) {}
