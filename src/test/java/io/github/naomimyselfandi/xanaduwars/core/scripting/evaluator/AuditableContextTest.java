package io.github.naomimyselfandi.xanaduwars.core.scripting.evaluator;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class AuditableContextTest {

    @Test
    void testToString(SeededRng random) {
        var name0 = random.nextString();
        var value0 = random.nextString();
        var name1 = random.nextString();
        var value1 = random.nextString();
        var name2 = random.nextString();
        var value2 = random.nextString();
        var name3 = random.nextString();
        var root = random.nextString();
        var context = new AuditableContext();
        context.setRootObject(root);
        context.setVariable(name0, new Object());
        context.setVariable(name1, new Object());
        context.setVariable(name2, new Object());
        context.setVariable(name3, new Object());
        context.setVariable(name0, value0);
        context.setVariable(name1, value1);
        context.setVariable(name2, null);
        context.setVariable(name2, value2);
        context.setVariable(name3, null);
        context.setVariable(null, null);
        var template = "{root=%s, %s=%s, %s=%s, %s=%s}";
        assertThat(context).hasToString(template, root, name0, value0, name1, value1, name2, value2);
    }

}
