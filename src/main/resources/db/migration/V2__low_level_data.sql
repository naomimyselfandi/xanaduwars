CREATE TYPE GAME_KIND AS ENUM ('GAME', 'MAP');

CREATE TABLE ll_data (
    id UUID PRIMARY KEY,
    kind GAME_KIND NOT NULL,
    width INTEGER NOT NULL CHECK (width > 0),
    next_unit_id INTEGER NOT NULL CHECK (next_unit_id >= 0),

    -- GAME-specific
    version_number TEXT,
    turn INTEGER,
    active_player INTEGER CHECK (active_player >= 0),

    -- MAP-specific
    map_name VARCHAR(32) CHECK (map_name <> ''),

    CHECK (
        (kind = 'GAME' AND turn IS NOT NULL AND active_player IS NOT NULL AND version_number IS NOT NULL AND map_name IS NULL)
     OR (kind = 'MAP'  AND turn IS NULL AND active_player IS NULL AND version_number IS NULL AND map_name IS NOT NULL)
    ),

    UNIQUE (map_name)
);

CREATE TABLE player_data (
    parent_id UUID NOT NULL REFERENCES ll_data(id) ON DELETE CASCADE,
    player_id INTEGER NOT NULL CHECK (player_id >= 0),
    PRIMARY KEY (parent_id, player_id),

    team INTEGER NOT NULL CHECK (team >= 0),
    defeated BOOLEAN NOT NULL DEFAULT FALSE,
    player_type INTEGER NOT NULL CHECK (player_type >= 0),
    resources JSONB NOT NULL DEFAULT '{}',
    known_spells JSONB NOT NULL DEFAULT '[]',
    active_spells JSONB NOT NULL DEFAULT '[]'
);

CREATE TABLE tile_data (
    parent_id UUID NOT NULL REFERENCES ll_data(id) ON DELETE CASCADE,
    tile_id INTEGER NOT NULL,
    PRIMARY KEY (parent_id, tile_id),

    tile_type INTEGER NOT NULL CHECK (tile_type >= 0),
    structure_type INTEGER CHECK (structure_type >= 0),
    owner INTEGER CHECK (owner >= 0),
    hitpoints INTEGER DEFAULT 100 NOT NULL CHECK (hitpoints >= 0 AND hitpoints <= 100),

    construction_structure_type INTEGER CHECK (construction_structure_type >= 0),
    construction_progress INTEGER CHECK (construction_progress >= 0 AND construction_progress <= 100),

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
    location INTEGER NOT NULL,
    can_act BOOLEAN NOT NULL,
    hitpoints INTEGER NOT NULL DEFAULT 100
);
