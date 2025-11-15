# Recycler

**Languages:** [English](README.md) · [Українська](docs/readme/README.uk.md) · [Русский](docs/readme/README.ru.md) · [Беларуская](docs/readme/README.be.md) · [Polski](docs/readme/README.pl.md) · [Eesti](docs/readme/README.et.md) · [Deutsch](docs/readme/README.de.md)

Recycler is a lightweight Paper 1.21.9 plugin that lets players gamble raw materials for surprise rewards while guarding per-material cooldowns.

## Features

- `/recycle` opens a single-row GUI titled **Item Recycler** with only slot 1 available for input.
- Any stack can be exchanged for a random block reward; the payout size respects the reward block's max stack limit.
- Dropping a **full stack** (exact max size and at least 16 items) converts the roll into a random item instead of a block.
- Item rewards have a 70% chance to roll enchantments using a configurable weighted table (default 1–5 enchants with heavier bias toward 2–3) and a 5% chance to double up when the item is stackable.
- High-tier items (netherite, diamond, gold, iron, stone, chainmail) use configurable tier multipliers that make powerful gear both rarer and harder to roll with ≥3 enchants (netherite defaults to ~10× rarer than neutral drops).
- Every material (both inputs and rewards) keeps its own 5-minute cooldown so you can't immediately repeat the same combo.
- The Recycler GUI stays open after each roll so you can keep feeding it.
- Items left inside are always returned to the player on close to avoid loss.

## Localization

- Built-in locales: `en_us`, `uk_ua`, `ru_ru`, `be_by`, `pl_pl`, `et_ee`, `de_de`.
- Messages live in `src/main/resources/lang/*.yml`; add more by mirroring the same keys and restarting/reloading.
- Players automatically see messages in their client language when a matching locale file exists.

| Locale | Canonical Name |
| --- | --- |
| `en_us` | Recycler |
| `uk_ua` | Перероблювач |
| `ru_ru` | Переработчик |
| `be_by` | Перапрацоўшчык |
| `pl_pl` | Przetwarzacz |
| `et_ee` | Taaskasutaja |
| `de_de` | Recyclinggerät |

## Building

Run the following Maven command in the repository root to compile the plugin:

```powershell
mvn package
```

The resulting jar will be located at `target/recycler-<version>.jar` (e.g., `target/recycler-v3.jar`).

### Containerized build options

- **Dockerfile**: `docker build -t recycler:local .` compiles the plugin inside a clean Temurin+Maven image and drops the jar in `/opt/recycler` of the resulting image.
- **Docker Compose**: `docker compose run --rm recycler-build` mounts the current repo into a Maven container, reusing a cached `.m2` volume.

## Testing

- To execute the automated test suite (JUnit 5 via Maven Surefire), run:

```powershell
mvn test
```

- Maven will look for test sources under `src/test/java`. Create packages that mirror the main source tree (e.g., `com.example.recycler.reward`) and add `*Test` classes that import `org.junit.jupiter.api.*`.
- Prefer fast, deterministic tests that target the modular reward helpers (tier weighting, enchantment rolls, cooldown calculations). Mock Bukkit types when necessary or wrap logic into plain Java services so they remain testable without a server runtime.
- Whenever you add or modify tests, re-run `mvn test` to ensure the project stays green before packaging.

### Version bump helper

`scripts/bump_version.py` auto-increments the `<version>` inside `pom.xml` (format `v<number>`). Use `python scripts/bump_version.py --dry-run` to preview the next number locally; the CI workflow runs it automatically before tagging a release.

### CI/CD overview

- `.github/workflows/release.yml` triggers on pushes to `main` or manual dispatch.
- Steps: Maven verify → compute & bump version → package jar → build the Docker image → commit + tag → publish a GitHub Release with the fresh jar.
- Version bump commits include `[skip ci]`, preventing infinite workflow loops while still keeping `pom.xml` in sync.

## Usage

1. Drop the compiled JAR into your server's `plugins/` folder.
2. Start the server on Paper 1.21.9 with Java 17.
3. Use `/recycle` to open the GUI, place the required stack in slot 1, and receive the reward.
4. Attempting to recycle while on cooldown will show a cooldown message; closing with an item inside returns it safely.
