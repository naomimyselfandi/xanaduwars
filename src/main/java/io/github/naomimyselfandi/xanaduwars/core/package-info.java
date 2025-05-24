/// Core game logic and models.
///
/// The in-game message bus warrants a special mention. Game messages are called
/// *queries* because, like SQL queries, they may have side effects, return
/// values, or do both. This makes game rules very flexible: the same mechanism
/// can define both conventional event handlers and distributed, collaborative
/// calculations.
///
/// @implNote The interfaces in this package use Jakarta validation annotations
/// as documentation. Implementations are encouraged to provide more exhaustive
/// validation.
@NonNullApi
package io.github.naomimyselfandi.xanaduwars.core;

import org.springframework.lang.NonNullApi;
