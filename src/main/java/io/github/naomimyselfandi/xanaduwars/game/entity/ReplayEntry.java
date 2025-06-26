package io.github.naomimyselfandi.xanaduwars.game.entity;

import io.github.naomimyselfandi.xanaduwars.core.gamestate.dto.CommandDto;
import io.github.naomimyselfandi.xanaduwars.core.gamestate.entity.GameStateData;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.List;

/// A description of a single turn in a replay.
@Data
@Embeddable
public class ReplayEntry implements Serializable {

    /// A snapshot of the start of the turn.
    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "snapshot_id", referencedColumnName = "id")
    private GameStateData snapshot;

    /// The commands executed during this turn.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<CommandDto> commands = List.of();

}
