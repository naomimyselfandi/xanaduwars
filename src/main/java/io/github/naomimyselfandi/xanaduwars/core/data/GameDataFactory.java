package io.github.naomimyselfandi.xanaduwars.core.data;

import io.github.naomimyselfandi.xanaduwars.core.wrapper.VersionNumber;

/// A factory that creates low-level game data.
public interface GameDataFactory {

    GameData create(GameData source);

    GameData create(GameMap map, VersionNumber VersionNumber);

}
