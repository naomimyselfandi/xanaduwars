package io.github.naomimyselfandi.xanaduwars.core.gameplay.queries;

import io.github.naomimyselfandi.xanaduwars.core.gameplay.state.Unit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class FirepowerQueryTest {

    @Mock
    private Unit subject, target;

    @InjectMocks
    private FirepowerQuery fixture;

    @Test
    void defaultValue() {
        assertThat(fixture.defaultValue()).isOne();
        verifyNoInteractions(subject, target);
    }

}
