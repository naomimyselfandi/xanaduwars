CREATE TABLE game_map (
    "id" UUID PRIMARY KEY,
    "name" VARCHAR(64) UNIQUE NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'UNPUBLISHED',
    "author_id" UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    "width" INTEGER NOT NULL,
    "height" INTEGER NOT NULL
);

CREATE TABLE map_tile (
    "map_id" UUID NOT NULL REFERENCES game_map(id) ON DELETE CASCADE,
    "index" INTEGER NOT NULL,
    "type" TEXT NOT NULL,
    "unit_type" TEXT,
    "unit_owner" INTEGER NOT NULL
);

CREATE TABLE player_slot (
    "map_id" UUID NOT NULL REFERENCES game_map(id) ON DELETE CASCADE,
    "index" INTEGER NOT NULL,
    "team" INTEGER NOT NULL
);
