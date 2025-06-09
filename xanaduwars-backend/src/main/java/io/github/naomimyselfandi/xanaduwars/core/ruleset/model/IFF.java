package io.github.naomimyselfandi.xanaduwars.core.ruleset.model;

/// An "identification, friend or foe" value.
public enum IFF {

    /// An IFF matching actors associated with the same player.
    SELF,

    /// An IFF matching actors on the same team.
    FRIEND,

    /// An IFF matching actors on different team.
    FOE,

}
