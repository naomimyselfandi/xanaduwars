/// The game message bus. Messages come in two varieties: events and queries.
/// Events are simple notifications, and their handlers typically have side
/// effects. Queries carry values with them, and their handlers modify those
/// values and are otherwise pure. This allows the game rule mechanism to
/// support both ordinary events and collaborative calculations.
@NonNullApi
package io.github.naomimyselfandi.xanaduwars.core.message;

import org.springframework.lang.NonNullApi;
