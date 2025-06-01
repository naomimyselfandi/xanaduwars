package io.github.naomimyselfandi.xanaduwars.gameplay.runtime.data;

import io.github.naomimyselfandi.xanaduwars.gameplay.value.Version;

/// A factory that creates low-level game data.
public interface GameDataFactory {

    GameData create(GameData source);

    GameData create(MapData map, Version version);

}
