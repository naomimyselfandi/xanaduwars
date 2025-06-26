package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.util.ConflictException;

interface GameStarter {

    void start(Game game) throws ConflictException;

}
