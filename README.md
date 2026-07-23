# Finnhub Quotes

Android app for realtime stock/forex quotes, built on the Finnhub REST + WebSocket API.

## Features

- **Watchlist** — realtime WebSocket price updates, swipe-to-delete, drag-to-reorder, pull-to-refresh REST fallback
- **Search** — debounced symbol/forex search with one-tap add/remove from the watchlist
- **Detail** — candlestick chart (multiple resolutions), company profile, financial metrics, peers, news
- **Price Alerts** — target-price alerts checked every 15 minutes via WorkManager, with system notifications
- **Settings** — light/dark/system theme, live-switching

## Architecture

Clean Architecture + feature modules + MVI (State/Intent/Effect): each `feature/*` module owns a ViewModel exposing a single `StateFlow` of UI state plus a `Flow` of one-off effects, driven by `domain`-layer UseCases that depend only on `domain`-layer Repository interfaces — never on `data`-layer implementations directly.

```
app/                  Hilt Application, MainActivity, Navigation graph, Settings screen
core/
  common/              AppResult, UiError, AssetType, AlertCondition, AppDispatchers, AppCoroutineScope
  domain/              UseCases, domain models, Repository interfaces (pure Kotlin, no Android dep)
  ui/                  Theme, design tokens, common Compose components, formatters
  network/             Retrofit/OkHttp, Finnhub REST DTOs and service
  websocket/           FinnhubWebSocketManager, reconnect backoff, subscription sync
  database/            Room entities/DAOs (watchlist, quote cache, candle cache, price alerts)
  datastore/           DataStore Preferences (refresh interval, theme mode)
  data/                Repository implementations, composing network+websocket+database+datastore
feature/
  watchlist/           Watchlist screen + ViewModel
  search/               Search screen + ViewModel
  detail/               Detail screen (chart/profile/news/peers) + ViewModel
  alert/                Alert list/create screens, ViewModels, WorkManager worker, notifications
```

## Setup

1. Copy `local.properties.sample` to `local.properties` (already git-ignored).
2. Add your Finnhub API key: `FINNHUB_API_KEY=<your key>` (get one free at https://finnhub.io).
3. Open in Android Studio or run `./gradlew assembleDebug`.

Without a valid API key, the app builds and runs, but every network call returns 401 — the empty-watchlist and empty-search states still work offline.

## Testing

```bash
./gradlew test         # unit tests across all 13 modules (JUnit5 + Robolectric where an Android Context is needed)
./gradlew lint          # Android Lint
```

No emulator/device is required for the full test suite.

## Tech stack

Kotlin 1.9.24 · Jetpack Compose (Material 3) · Coroutines/Flow · Hilt · Retrofit + OkHttp + kotlinx.serialization · Room · DataStore · WorkManager · Navigation Compose (type-safe routes) · Chart geometry hand-rolled with Compose Canvas (candlestick chart) · JUnit5 + MockK + Turbine + MockWebServer + Robolectric.
