package io.github.naomimyselfandi.xanaduwars.account.value;

/// A *raw, unhashed* password or API key. Use with caution!
public sealed interface Plaintext<T extends Hash> extends Secret permits PlaintextAPIKey, PlaintextPassword {

    /// Construct the hashed representation of this plaintext.
    ///
    /// @apiNote Implementations of this interface are simple records without
    /// direct access to the hashing service. This method expects the hash to
    /// be computed externally and provided. It is useful primarily for its
    /// return type, which ensures the correct type of secret is generated.
    T toHash(String hashedValue);

}
