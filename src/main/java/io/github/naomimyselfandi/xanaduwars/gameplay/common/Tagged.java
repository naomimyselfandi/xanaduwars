package io.github.naomimyselfandi.xanaduwars.gameplay.common;

/// An object with a name and a tag sets.
public interface Tagged {

    /// This object's name. Implementations should describe any uniqueness
    /// guarantees their names provide.
    Name name();

    /// Any tags that apply to this object.
    TagSet tags();

}
