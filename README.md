# AndroidTechMix

Modern Android sample that searches GitHub users, shows profile details with top repositories, and keeps favorites offline.

Built as a portfolio-ready codebase for senior Android interviews: Clean Architecture, modularization, offline-first Single Source of Truth, and production-style testing.

## Features

- **Search** — debounced GitHub user search with Paging 3 + RemoteMediator cache
- **User detail** — profile stats, bio, metadata, top repositories, share, favorite
- **Favorites** — Room-backed offline list

## Architecture

```
:app
 ├── :feature:search
 ├── :feature:favorites
 ├── :feature:userdetail
 ├── :core:ui
 ├── :core:domain   (pure Kotlin models / use cases / repository contracts)
 ├── :core:data     (repository impl + RemoteMediator)
 │    ├── :core:network   (Retrofit + Hilt NetworkModule)
 │    └── :core:database  (Room + Hilt DatabaseModule)
 ├── :core:common
 └── :core:testing
```

- **Presentation** — Jetpack Compose, UDF (`UiState` / `UiEvent` / `UiEffect`), Hilt ViewModels, **type-safe Navigation**
- **Domain** — models, repository contracts, use cases
- **Data** — Room as Single Source of Truth, Retrofit writes into Room (RemoteMediator)

```
UI  →  UseCase  →  Repository  →  Room (SSOT)
                              ↘  GitHub API
```

Networking failures are mapped to `AppError` / `AppException` in the data/network layers so feature modules never depend on Retrofit.
## Stack

| Area | Library |
|------|---------|
| Language | Kotlin 2.1 + Coroutines / Flow |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Networking | Retrofit + OkHttp + Kotlin Serialization |
| Persistence | Room |
| Lists | Paging 3 |
| Images | Coil |
| Build | Version Catalog + convention plugins (`build-logic`) |
| Tests | JUnit, Truth, Turbine, MockK, Compose UI Test |

## Setup

1. Clone the repository
2. Open in Android Studio (Ladybug / latest stable)
3. Optional — raise GitHub API rate limits by adding a PAT to `local.properties`:

```properties
sdk.dir=/path/to/Android/sdk
github.token=ghp_your_token_here
```

4. Sync Gradle and run the `app` configuration

Without a token, unauthenticated requests are limited to **60 requests/hour**.

## Testing

```bash
./gradlew test
./gradlew :app:assembleDebug
# Instrumented (emulator/device required):
./gradlew :app:connectedDebugAndroidTest
```

## API

- `GET /search/users`
- `GET /users/{username}`
- `GET /users/{username}/repos`

## License

MIT
