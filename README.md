# Xanadu Wars

*Xanadu Wars* is a turn-based strategy game inspired by the *Advance Wars* series, designed for asynchronous, long-running games in a "play-by-post" style. This project emphasizes robustness, flexibility, and forward compatibility, with a backend architecture intended to support long-lived games even as the rules evolve.

> Note: This project is in *early* development and not yet playable. See the [Goals](#goals) and [Architecture](#architecture) sections for more information.

## Goals

*Xanadu Wars* aims to solve common challenges in asynchronous strategy games:

- Asynchronous Play: Players can take their turn whenever it's convenient. Games may last several real-time days or weeks.
- Replay Support: Games are stored as an initial state and a list of (deterministic) actions, allowing any point in the game to be reconstructed. Snapshots are taken at the end of each turn.
- Versioning and Compatibility: Games are tied to the version of the ruleset they started with. Rule updates don’t affect in-progress games.
- Flexible Mechanics: Units, spells, tiles, and game rules are all data-driven, allowing for...
- Diverse Characters and Spells: Every playable commander has their own unique rules. A wide range of spells provides even more variety.
- Developer Experience: A robust tech stack and clean, testable architecture helps ensure correctness, even when unusual situations and spell combinations are involved.

## Architecture

*Xanadu Wars* is fundamentally a web application. A browser-based client interacts with a REST API that provides access to game states. All game logic is defined in the backend. A redaction layer ensures that players can never see data they shouldn't, even if they inspect API responses manually.

### Language & Frameworks

- Java 23
- Spring Boot
- JPA (Hibernate) + Flyway
- Maven (build), JUnit 5 (tests)

Knowledge of the [tiny types pattern](https://www.reddit.com/r/programming/comments/2s87ey) and my Dev.to article on [field iteration](https://dev.to/naomimyselfandi/lombok-field-iteration-safer-object-copying-in-java-2mp1) will be helpful for anyone getting involved in this project.

### Key Concepts

- Query Evaluator: Game logic is wired together using a message bus, allowing game rules to coordinate without direct knowledge of each other. Messages include both simple events and collaborative calculations; they're called "queries" to reflect this unique flexibility.
- Versioned Games: Each game tracks the version of the game it was created with. Releasing a new version of the game has no effect on ongoing games. Internal playtest versions are supported.
- Data-Driven Rules: Each game version is defined using modular JSON. Versions include only the configuration that's changed and inherit the rest from the previous version, keeping changes clear and manageable.
- Game History: Each game tracks its initial state and the actions taken by players, allowing any point in the game to be reconstructed exactly. Snapshots are taken at the start of every turn as a performance optimization.
- Player-Specific Views: A game state can be viewed a specific player by copying what that player sees into an in-memory game state, allowing for preview functionality and action validation without the risk of leaking information.

### Implemented

- Entity model for units, tiles, and players
- Schema migrations with Flyway
- Integration tests using Testcontainers
- Deterministic random number generation with my own [SeededRandom](https://github.com/naomimyselfandi/seeded-random) library
- Support for in-memory and persistent game state
- Version tracking per game

### Planned

- Turn submission and processing
- Replay and diff serialization
- REST API for player actions
- Spell and ability system
- Visibility and fog-of-war mechanics
- Web-based game viewer (eventually)

## Testing

Tests use deterministic randomness via SeededRandom to support reproducible test cases across repeated runs. Integration tests use Testcontainers for full-stack persistence verification against PostgreSQL. Replay functionality provides robust regression testing.

## License

This project is licensed under GPL-3.0. You’re free to use, modify, and share it — but if you distribute a modified version, you must do so under the same license.

