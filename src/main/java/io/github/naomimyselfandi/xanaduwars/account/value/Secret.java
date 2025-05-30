package io.github.naomimyselfandi.xanaduwars.account.value;

/// A plaintext or hashed password or API key.
///
/// @implSpec Implementations must override `toString` to return a constant
/// value to prevent secrets being leaked in logs.
public sealed interface Secret permits Hash, Plaintext {

    /// The string form of this secret. Use with caution!
    String text();

}
