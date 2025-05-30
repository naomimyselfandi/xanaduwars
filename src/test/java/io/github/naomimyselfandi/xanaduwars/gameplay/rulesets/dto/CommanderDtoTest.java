package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.dto;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import io.github.naomimyselfandi.xanaduwars.gameplay.Commander;
import io.github.naomimyselfandi.xanaduwars.gameplay.SpellType;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.TagSet;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SeededRandomExtension.class})
class CommanderDtoTest {

    @Mock
    private Commander source;

    @Mock
    private SpellType<?> foo, bar;

    @Test
    void mappings(SeededRng random) {
        when(source.id()).thenReturn(random.nextCommanderId());
        when(source.name()).thenReturn(random.nextName());
        when(source.tags()).thenReturn(TagSet.of(random.nextTag(), random.nextTag()));
        when(source.signatureSpells()).thenReturn(List.of(foo, bar));
        when(foo.id()).thenReturn(random.nextSpellTypeId());
        when(bar.id()).thenReturn(random.nextSpellTypeId());
        var dto = new CommanderDto(source);
        assertThat(dto.getId()).isEqualTo(source.id());
        assertThat(dto.getName()).isEqualTo(source.name());
        assertThatCollection(dto.getTags()).isEqualTo(source.tags());
        assertThat(dto.getSignatureSpells()).containsExactly(foo.id(), bar.id());
    }

}
