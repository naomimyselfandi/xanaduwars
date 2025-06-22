# Xanadu Wars

*Xanadu Wars* is a turn-based strategy game inspired by the *Advance Wars*
series, designed for asynchronous, long-running games in a "play-by-post" style.
This project emphasizes robustness, flexibility, and forward compatibility, with
a backend architecture intended to support long-lived games even as the rules
evolve.

> Note: This project is in *early* development and not yet playable. See the
> [Goals](#goals) and [Architecture](#architecture) sections for more
> information. Also, on June 22, 2025, I reinitialized the repository to work
> around a technical issue - don't be surprised by the lack of commit history
> before then.

## Goals

*Xanadu Wars* aims to solve common challenges in asynchronous strategy games:

- Asynchronous Play: Players can take their turn whenever it's convenient. Games 
  may last several real-time days or weeks.
- Replay Support: Games are stored as an initial state and a list of
  (deterministic) actions, allowing any point in the game to be reconstructed.
  Snapshots are taken at the end of each turn.
- Versioning and Compatibility: Games are tied to the version of the ruleset
  they started with. Rule updates don’t affect in-progress games (or replays).
- Flexible Mechanics: Units, spells, tiles, and game rules are all data-driven,
  allowing for...
- Diverse Characters and Spells: Every playable commander has their own unique
  mechanics. A wide range of spells provides even more variety.
- Developer Experience: A robust tech stack and clean, testable architecture
  helps ensure correctness, even when unusual situations and spell combinations
  are involved.

## Architecture

*Xanadu Wars* is fundamentally a web application. A browser-based client
interacts with a REST API that provides access to game states. All game logic is
defined in the backend, and a redaction layer ensures that players can never see
data they shouldn't, even if they inspect API responses manually.

Internally, *Xanadu Wars* uses a scripting language derived from SpEl to specify
game rules. Rules function as event handlers, although some of these "events"
return values, allowing rules to cleanly interact with unit statistics. The word
"query" is used, by analogy to SQL queries, to reflect this unusual flexibility.

### Language & Frameworks

- Java 23
- Spring Boot
- JPA (Hibernate) + Flyway
- Maven (build), JUnit 5 (tests)
- MapStruct

This project uses the [tiny types pattern](https://www.reddit.com/r/programming/comments/2s87ey),
facilitated by Hibernate's support for `@Embeddable` on `record` types.

### Testing

Tests use deterministic randomness via my own [SeededRandom](https://github.com/naomimyselfandi/seeded-random)
to support reproducible test cases across repeated runs. Integration tests use
Testcontainers for full-stack persistence verification against PostgreSQL.
Replay functionality provides robust regression testing.

## License

This project is licensed under GPL-3.0. You’re free to use, modify, and share it — but if you distribute a modified version, you must do so under the same license.

