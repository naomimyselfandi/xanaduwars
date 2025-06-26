package io.github.naomimyselfandi.xanaduwars.game.service;

import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.core.ruleset.service.Version;
import io.github.naomimyselfandi.xanaduwars.game.entity.Game;
import io.github.naomimyselfandi.xanaduwars.map.dto.MapDto;
import io.github.naomimyselfandi.xanaduwars.util.Id;

interface GameFactory {

    Game createAdHoc(Id<Account> accountId, MapDto map, Version version);

}
