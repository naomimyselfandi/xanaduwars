package io.github.naomimyselfandi.xanaduwars.core.messages;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.script.ScriptRuntime;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class SimpleQueryTest {

    @Test
    void resultType() {
        var query = new SimpleQuery<List<String>>() {

            @Override
            public @NotNull List<String> defaultValue(@NotNull ScriptRuntime runtime) {
                throw new UnsupportedOperationException();
            }

        };
        assertThat(query.resultType()).satisfies(it -> {
            assertThat(it.getType()).isEqualTo(List.class);
            assertThat(it.nested(1)).isEqualTo(TypeDescriptor.valueOf(String.class));
        });
    }

}
