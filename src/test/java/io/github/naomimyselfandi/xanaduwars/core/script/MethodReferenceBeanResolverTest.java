package io.github.naomimyselfandi.xanaduwars.core.script;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.EvaluationContext;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class MethodReferenceBeanResolverTest {

    @Mock
    private EvaluationContext context;

    private MethodReferenceBeanResolver fixture;

    @BeforeEach
    void setup() {
        fixture = new MethodReferenceBeanResolver();
    }

    @Test
    void resolve(SeededRng random) {
        var name = random.nextString();
        assertThat(fixture.resolve(context, name)).isEqualTo(new MethodReference(context, name));
    }

}
