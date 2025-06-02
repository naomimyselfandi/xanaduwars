package io.github.naomimyselfandi.xanaduwars.gameplay;

/// An element which can take actions.
public sealed interface Actor extends Element permits Player, Node {}
