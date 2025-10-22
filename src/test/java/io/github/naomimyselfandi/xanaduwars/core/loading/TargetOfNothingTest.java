package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.databind.node.NullNode;
import io.github.naomimyselfandi.xanaduwars.core.model.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TargetOfNothingTest {

    private final TargetOfNothing fixture = TargetOfNothing.NOTHING;

    @Mock
    private Unit actor;

    @Test
    void unpack() {
        assertThat(fixture.unpack(actor, NullNode.getInstance())).isEqualTo(fixture);
    }

    @Test
    void validate() {
        assertThat(fixture.validate(actor, fixture)).isTrue();
    }

    @Test
    void propose() {
        assertThat(fixture.propose(actor)).containsExactly(fixture);
    }

    @Test
    void pack() {
        assertThat(fixture.pack(fixture)).isEqualTo(NullNode.getInstance());
    }

}
