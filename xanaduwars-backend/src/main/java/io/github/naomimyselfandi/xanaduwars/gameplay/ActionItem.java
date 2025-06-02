package io.github.naomimyselfandi.xanaduwars.gameplay;

/// A pair of an action and an appropriate target.
public record ActionItem<S extends Actor, T>(Action<S, T> action, T target) {}
