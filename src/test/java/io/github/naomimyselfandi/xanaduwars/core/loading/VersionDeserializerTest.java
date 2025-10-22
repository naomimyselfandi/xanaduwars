package io.github.naomimyselfandi.xanaduwars.core.loading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;
import io.github.naomimyselfandi.xanaduwars.core.service.VersionService;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VersionDeserializerTest {

    private ObjectMapper objectMapper;

    @Mock
    private Version version;

    @Mock
    private VersionService versionService;

    @InjectMocks
    private VersionDeserializer versionDeserializer;

    @BeforeEach
    void setup() {
        var module = new SimpleModule().addDeserializer(Version.class, versionDeserializer);
        objectMapper = new ObjectMapper().registerModule(module);
    }

    @Test
    void deserialize(SeededRng random) throws JsonProcessingException {
        var versionNumber = random.<VersionNumber>get();
        when(versionService.getVersion(versionNumber)).thenReturn(version);
        var json = objectMapper.writeValueAsString(versionNumber);
        assertThat(objectMapper.readValue(json, Version.class)).isEqualTo(version);
    }

}
