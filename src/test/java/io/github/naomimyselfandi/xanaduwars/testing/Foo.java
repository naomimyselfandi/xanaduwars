package io.github.naomimyselfandi.xanaduwars.testing;

import org.jetbrains.annotations.NotNull;

public record Foo(String foo) {

    public Foo {
        if (foo.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public @NotNull String toString() {
        return foo;
    }

}
