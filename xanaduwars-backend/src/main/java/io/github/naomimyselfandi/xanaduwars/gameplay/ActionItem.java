package io.github.naomimyselfandi.xanaduwars.gameplay;

/// A pair of an action and an appropriate target.
public record ActionItem<S extends Element, T>(Action<S, T> action, T target) {}
