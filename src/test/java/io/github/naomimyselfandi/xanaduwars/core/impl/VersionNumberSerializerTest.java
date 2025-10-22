package io.github.naomimyselfandi.xanaduwars.core.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.core.model.Version;
import io.github.naomimyselfandi.xanaduwars.core.model.VersionNumber;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class VersionNumberSerializerTest {

    @Mock
    private Version version;

    @Test
    void serialize(SeededRng random) throws JsonProcessingException {
        record Helper(@JsonSerialize(using = VersionNumberSerializer.class) Version version) {}
        record Helper2(VersionNumber version) {}
        var versionNumber = random.<VersionNumber>get();
        when(version.getVersionNumber()).thenReturn(versionNumber);
        var objectMapper = new ObjectMapper();
        var json = objectMapper.writeValueAsString(new Helper(version));
        assertThat(objectMapper.readValue(json, Helper2.class).version).isEqualTo(versionNumber);
    }

}
