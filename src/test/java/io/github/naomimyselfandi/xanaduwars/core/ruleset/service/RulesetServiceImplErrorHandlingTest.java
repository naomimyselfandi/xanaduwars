package io.github.naomimyselfandi.xanaduwars.core.ruleset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RulesetServiceImplErrorHandlingTest {

    private RulesetServiceImpl fixture;

    @BeforeEach
    void setup() {
        fixture = new RulesetServiceImpl("testRuleset", new ObjectMapper());
    }

    @Test
    void load_WhenAVersionDoesNotExist_ThenThrows() {
        assertThatThrownBy(() -> fixture.load(new Version("0.0.1")))
                .hasMessage("Failed loading ruleset for version 0.0.1.");
    }

}
