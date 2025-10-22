package io.github.naomimyselfandi.xanaduwars.core.message;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.script.Script;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class RuleTest {

    @Mock
    private MessageType messageType;

    @Mock
    private Script when, then;

    @Test
    void constructor() {
        assertThat(new Rule(messageType, when, then))
                .returns(messageType, Rule::on)
                .returns(when, Rule::when)
                .returns(then, Rule::then);
    }

    @Test
    void constructor_WhenTheSecondArgumentIsNull_ThenAppliesADefault() {
        assertThat(new Rule(messageType, null, then))
                .returns(messageType, Rule::on)
                .returns(Script.TRUE, Rule::when)
                .returns(then, Rule::then);
    }

}
