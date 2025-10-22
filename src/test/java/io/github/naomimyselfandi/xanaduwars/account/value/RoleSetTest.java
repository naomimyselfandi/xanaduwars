package io.github.naomimyselfandi.xanaduwars.account.value;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.testing.TestUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SeededRandomExtension.class)
class RoleSetTest {

    @Test
    void none() {
        assertThat(RoleSet.NONE).isEqualTo(RoleSet.of(List.of()));
    }

    @Test
    void asCollection(SeededRng random) {
        var role1 = random.pick(Role.values());
        var role2 = random.not(role1);
        assertThat(RoleSet.of(List.of(role1, role2)).asCollection()).containsOnly(role1, role2);
    }

    @Test
    void testToString(SeededRng random) {
        var role1 = random.pick(Role.values());
        var role2 = random.not(role1);
        var roles = RoleSet.of(List.of(role1, role2));
        assertThat(roles).hasToString(EnumSet.of(role1, role2).toString());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            DEVELOPER,MODERATOR,'["DEVELOPER","MODERATOR"]'
            MODERATOR,JUDGE,'["MODERATOR","JUDGE"]'
            """)
    void json(Role role1, Role role2, @Language("json") String json) {
        TestUtils.assertJson(RoleSet.of(List.of(role1, role2)), json);
    }

}
