package io.github.naomimyselfandi.xanaduwars.account.value;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

/// A *raw, unhashed* password. Use with caution!
public record PlaintextPassword(@NotNull @Length(min = 12) String text) {

    @JsonValue
    String preventLeak() {
        return toString();
    }

    @Override
    public String toString() {
        return "************";
    }

}
