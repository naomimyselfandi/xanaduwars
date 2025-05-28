/// A specialized message bus used for games. Game messages are called *queries*
/// because, like SQL queries, they may have side effects, return values, or do
/// both. This makes game rules very flexible: the same mechanism can define
/// both conventional event handlers and distributed, collaborative calculations.
///
/// @see io.github.naomimyselfandi.xanaduwars.gameplay.queries Common query
/// implementations.
@NonNullApi
package io.github.naomimyselfandi.xanaduwars.gameplay.query;

import org.springframework.lang.NonNullApi;
