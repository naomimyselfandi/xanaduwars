CREATE TABLE game_state (
    "id" UUID PRIMARY KEY,
    "version" TEXT NOT NULL,
    "turn" INTEGER NOT NULL DEFAULT 0 CHECK(turn >= 0),
    "next_unit" INTEGER NOT NULL CHECK (next_unit >= 0),
    "passed" BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE player_state (
    "game_state_id" UUID NOT NULL REFERENCES game_state(id) ON DELETE CASCADE,
    "player" INTEGER NOT NULL CHECK (player >= 0),
    PRIMARY KEY (game_state_id, player),

    "team" INTEGER NOT NULL CHECK (team >= 0),
    "commander" INTEGER CHECK (commander >= 0) DEFAULT 0,

    "supplies" INTEGER NOT NULL CHECK (supplies >= 0) DEFAULT 0,
    "aether" INTEGER NOT NULL CHECK (aether >= 0) DEFAULT 0,
    "focus" INTEGER NOT NULL CHECK (focus >= 0) DEFAULT 0,
    "defeated" BOOLEAN NOT NULL DEFAULT FALSE,

    "signature_spell_activation" INTEGER NOT NULL DEFAULT 0,
    "chosen_spell_activation" INTEGER NOT NULL DEFAULT 0,
    "chosen_spell_revelation" INTEGER NOT NULL DEFAULT 0,
    "chosen_spells" TEXT NOT NULL DEFAULT ''
);

CREATE TABLE structure_state (
    "game_state_id" UUID NOT NULL REFERENCES game_state(id) ON DELETE CASCADE,
    "x" INTEGER NOT NULL CHECK (x >= 0 AND x <= 999),
    "y" INTEGER NOT NULL CHECK (y >= 0 AND y <= 999),
    PRIMARY KEY (game_state_id, x, y),

    "structure_type" INTEGER NOT NULL CHECK (structure_type >= 0),
    "player" INTEGER CHECK (player >= 0),
    "hp" INTEGER NOT NULL CHECK (hp >= 0 AND hp <= 100),
    "incomplete" BOOLEAN NOT NULL
);

CREATE TABLE tile_state (
    "game_state_id" UUID NOT NULL REFERENCES game_state(id) ON DELETE CASCADE,
    "x" INTEGER NOT NULL CHECK (x >= 0 AND x <= 999),
    "y" INTEGER NOT NULL CHECK (y >= 0 AND y <= 999),
    PRIMARY KEY (game_state_id, x, y),

    "tile_type" INTEGER NOT NULL CHECK (tile_type >= 0),
    "memory" JSONB
);

CREATE TABLE unit_state (
    "game_state_id" UUID NOT NULL REFERENCES game_state(id) ON DELETE CASCADE,
    "unit" INTEGER NOT NULL CHECK (unit >= 0),
    PRIMARY KEY (game_state_id, unit),

    "unit_type" INTEGER NOT NULL CHECK (unit_type >= 0),
    "player" INTEGER CHECK (player >= 0),
    "location" VARCHAR(9) NOT NULL,
    "ready" BOOLEAN NOT NULL DEFAULT FALSE,
    "hp" INTEGER NOT NULL CHECK (hp >= 0 AND hp <= 100)
);
