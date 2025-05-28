CREATE TYPE GAME_KIND AS ENUM ('GAME', 'MAP');

CREATE TABLE ll_data (
    id UUID PRIMARY KEY,
    kind GAME_KIND NOT NULL,
    width INTEGER NOT NULL CHECK (width > 0),
    next_unit_id INTEGER NOT NULL CHECK (next_unit_id >= 0),

    -- GAME-specific
    version TEXT,
    turn INTEGER,
    active_player INTEGER CHECK (active_player >= 0),

    CHECK (
        (kind = 'GAME' AND turn IS NOT NULL AND active_player IS NOT NULL AND version IS NOT NULL)
     OR (kind = 'MAP'  AND turn IS NULL AND active_player IS NULL AND version IS NULL)
    )
);

CREATE TABLE player_data (
    parent_id UUID NOT NULL REFERENCES ll_data(id) ON DELETE CASCADE,
    player_id INTEGER NOT NULL CHECK (player_id >= 0),
    PRIMARY KEY (parent_id, player_id),

    team INTEGER NOT NULL CHECK (team >= 0),
    defeated BOOLEAN NOT NULL DEFAULT FALSE,
    commander INTEGER NOT NULL CHECK (commander >= 0),
    resources JSONB NOT NULL DEFAULT '{}',
    known_spells JSONB NOT NULL DEFAULT '[]',
    active_spells JSONB NOT NULL DEFAULT '[]'
);

CREATE TABLE tile_data (
    parent_id UUID NOT NULL REFERENCES ll_data(id) ON DELETE CASCADE,
    x INTEGER NOT NULL CHECK (x >= 0 AND x <= 999),
    y INTEGER NOT NULL CHECK (y >= 0 AND y <= 999),
    PRIMARY KEY (parent_id, x, y),

    tile_type INTEGER NOT NULL CHECK (tile_type >= 0),
    structure_type INTEGER CHECK (structure_type >= 0),
    owner INTEGER CHECK (owner >= 0),
    hitpoints DOUBLE PRECISION DEFAULT 1 NOT NULL CHECK (hitpoints >= 0 AND hitpoints <= 1),

    construction_structure_type INTEGER CHECK (construction_structure_type >= 0),
    construction_progress DOUBLE PRECISION CHECK (construction_progress >= 0 AND construction_progress <= 1),

    memory JSONB NOT NULL DEFAULT '{}',

    -- If construction is present, it must be fully defined.
    CHECK (
        (construction_structure_type IS NULL AND construction_progress IS NULL)
     OR (construction_structure_type IS NOT NULL AND construction_progress IS NOT NULL)
    )
);

CREATE TABLE unit_data (
    parent_id UUID NOT NULL REFERENCES ll_data(id) ON DELETE CASCADE,
    unit_id INTEGER NOT NULL CHECK (unit_id >= 0),
    PRIMARY KEY (parent_id, unit_id),

    unit_type INTEGER NOT NULL CHECK (unit_type >= 0),
    owner INTEGER CHECK (owner >= 0),
    location VARCHAR(7) NOT NULL,
    can_act BOOLEAN NOT NULL,
    hitpoints DOUBLE PRECISION DEFAULT 1 NOT NULL CHECK (hitpoints >= 0 AND hitpoints <= 1)
);
