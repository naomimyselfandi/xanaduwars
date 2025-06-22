package io.github.naomimyselfandi.xanaduwars.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.ChoiceDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GameplayTestScript {

    @JsonIgnore
    private String name;

    private MapDto map;

    private List<ChoiceDto> choices;

    private List<Step> steps;

    private Map<String, String> preconditions = new HashMap<>();

    @Data
    public static class Step {

        @JsonUnwrapped
        private CommandDto command;

        private boolean invalid;

        private List<String> setup = new ArrayList<>();

        private Map<String, String> postconditions = new HashMap<>();

    }

    @Override
    public String toString() {
        return name;
    }

}
