CREATE TABLE game_state_data (
    "id" UUID PRIMARY KEY,
    "version" TEXT NOT NULL,
    "turn" INTEGER NOT NULL DEFAULT 0,
    "active_player" INTEGER CHECK (active_player >= 0),
    "next_unit_id" INTEGER NOT NULL CHECK (next_unit_id >= 0),
    "pass" BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE player_data (
    "game_state_id" UUID NOT NULL REFERENCES game_state_data(id) ON DELETE CASCADE,
    "player_id" INTEGER NOT NULL CHECK (player_id >= 0),
    PRIMARY KEY (game_state_id, player_id),

    "team" INTEGER NOT NULL CHECK (team >= 0),
    "defeated" BOOLEAN NOT NULL DEFAULT FALSE,
    "commander" INTEGER NOT NULL CHECK (commander >= 0) DEFAULT 0,
    "supplies" INTEGER NOT NULL CHECK (supplies >= 0) DEFAULT 0,
    "aether" INTEGER NOT NULL CHECK (aether >= 0) DEFAULT 0,
    "focus" INTEGER NOT NULL CHECK (focus >= 0) DEFAULT 0,
    "spell_slots" TEXT NOT NULL DEFAULT ''
);

CREATE TABLE tile_data (
    "game_state_id" UUID NOT NULL REFERENCES game_state_data(id) ON DELETE CASCADE,
    "x" INTEGER NOT NULL CHECK (x >= 0 AND x <= 999),
    "y" INTEGER NOT NULL CHECK (y >= 0 AND y <= 999),
    PRIMARY KEY (game_state_id, x, y),

    "tile_type" INTEGER NOT NULL CHECK (tile_type >= 0),
    "memory" TEXT NOT NULL DEFAULT '',

    "structure_type" INTEGER CHECK (structure_type >= 0),
    "player_id" INTEGER CHECK (player_id >= 0),
    "hp" INTEGER CHECK (hp >= 0 AND hp <= 100),
    "complete" BOOLEAN,

    -- If a structure is present, it must be fully defined.
    CHECK ((structure_type IS NOT NULL) OR (player_id IS NULL)),
    CHECK ((structure_type IS NULL) = (hp IS NULL)),
    CHECK ((structure_type IS NULL) = (complete IS NULL))
);

CREATE TABLE unit_data (
    "game_state_id" UUID NOT NULL REFERENCES game_state_data(id) ON DELETE CASCADE,
    "unit_id" INTEGER NOT NULL CHECK (unit_id >= 0),
    PRIMARY KEY (game_state_id, unit_id),

    "unit_type" INTEGER NOT NULL CHECK (unit_type >= 0),
    "player_id" INTEGER CHECK (player_id >= 0),
    "location" VARCHAR(7) NOT NULL,
    "moved" BOOLEAN NOT NULL,
    "acted" BOOLEAN NOT NULL,
    "hp" INTEGER NOT NULL CHECK (hp >= 0 AND hp <= 100)
);
