CREATE TABLE game (
    "id" UUID PRIMARY KEY,
    "status" TEXT NOT NULL,
    "host_id" UUID NOT NULL REFERENCES account(id),
    "game_state_id" UUID NOT NULL REFERENCES game_state(id)
);

CREATE TABLE player_slots (
  "game_id" UUID NOT NULL REFERENCES game(id),
  "player" INTEGER NOT NULL CHECK(player >= 0),
  PRIMARY KEY (game_id, player),

  "account_id" UUID REFERENCES account(id),
  UNIQUE (account_id, game_id)
);

CREATE TABLE replay (
  "game_id" UUID NOT NULL REFERENCES game(id),
  "seq" INTEGER NOT NULL CHECK(seq >= 0),
  PRIMARY KEY (game_id, seq),

  "snapshot_id" UUID NOT NULL REFERENCES game_state(id),
  "commands" JSONB NOT NULL DEFAULT '[]'
);
