package io.github.naomimyselfandi.xanaduwars.gameplay.value;

import org.jetbrains.annotations.Contract;

record ScalarNotPercent(double doubleValue) implements Scalar {

    ScalarNotPercent(double doubleValue) {
        if (doubleValue < 0 || doubleValue > 1) {
            this.doubleValue = doubleValue;
        } else {
            var message = "%s should be represented as an instance of Percent".formatted(doubleValue);
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    @Contract(pure = true)
    public Percent clamp() {
        return doubleValue < 0 ? Percent.ZERO : Percent.FULL;
    }

}
