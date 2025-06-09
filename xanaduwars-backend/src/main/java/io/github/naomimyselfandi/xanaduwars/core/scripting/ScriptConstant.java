package io.github.naomimyselfandi.xanaduwars.core.scripting;

/// A global script constant.
public interface ScriptConstant {

    /// This constant's string form. This is how the constant is referred to in
    /// scripts, so it must be semantically meaningful and syntactically valid.
    @Override String toString();

}
