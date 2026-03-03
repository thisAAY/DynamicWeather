# Dynamic Weather

An Android weather app with an **AI-driven, server-side-rendered UI**. Instead of a fixed layout, the entire screen is described by a JSON component tree that an AI agent (powered by [Koog AI](https://koog.ai) + OpenAI) can reshape on demand in response to natural-language requests.

---

## Concept

Most weather apps have a hard-coded UI. This one doesn't.

The screen layout is defined as a JSON tree of components — rows, columns, cards, text, icons, weather cards, stat displays, progress bars, gradients, and more. A Koog AI agent holds the current layout JSON as its context and can rewrite it entirely when the user asks. The app re-renders the new JSON instantly using a recursive Jetpack Compose renderer.

```
User: "Make it cyberpunk neon with dark background"
  → AI agent receives current layout JSON + user request
  → Returns a new JSON layout
  → UI re-renders
```

---

## Features

- Live weather data from OpenWeatherMap (current conditions for any city)
- Default city: **Cairo, Egypt**
- Search any city worldwide
- AI-powered layout redesign via a FAB + dialog
- Stateless agent — every request gets full current JSON context, no history drift
- Rich component library with low-level primitives and smart high-level weather widgets

---

## Architecture

```
app/
├── agent/               Koog AI layout agent + system prompt / JSON schema
├── data/
│   ├── remote/          Ktor HttpClient → OpenWeatherMap API
│   └── repository/      Repository implementation
├── domain/
│   ├── model/           WeatherData, UIComponent sealed class tree
│   ├── repository/      WeatherRepository interface
│   └── LayoutDefaults   Default Cairo layout JSON
└── presentation/
    ├── viewmodel/        WeatherViewModel (StateFlows: layout, weather, loading, agent)
    ├── renderer/         DynamicUIRenderer + HighLevelComponents + ColorUtils + IconUtils
    └── screen/           WeatherScreen + LayoutRequestDialog
```

### Data flow

```
App launch
  → ViewModel loads default layout JSON (Cairo)
  → Ktor fetches Cairo weather from OpenWeatherMap
  → DynamicUIRenderer parses JSON tree → renders Compose UI

User searches a city
  → search_bar fires actionKey "search_city"
  → ViewModel.onAction() → fetches new weather data
  → UI auto-updates (WeatherCard / WeatherStat etc. re-bind to new data)

User taps FAB → "Redesign"
  → Types request (e.g. "Minimal dark mode, huge temperature only")
  → LayoutAgent sends: system prompt + current JSON + request → OpenAI GPT-4o
  → Returns new JSON string → ViewModel validates + updates layout StateFlow
  → DynamicUIRenderer re-renders the whole screen
```

---

## Component Library

The AI agent (and your own JSON) can use any combination of these:

### Low-Level Primitives

| Type | Description |
|---|---|
| `column` | Vertical stack with arrangement + alignment |
| `row` | Horizontal stack |
| `box` | Stacking container (children layered, with `contentAlignment`) |
| `card` | Material3 elevated card |
| `text` | Text with full Material typography scale + hex color |
| `spacer` | Fixed `height` dp or flexible `weight` fraction |
| `icon` | Material icon (cloud, air, water\_drop, thermostat, visibility, wb\_sunny, …) |
| `divider` | Horizontal rule |
| `badge` | Colored pill label (custom `backgroundColor` + `textColor`) |
| `chip` | AssistChip with optional leading icon |
| `progress_bar` | Static linear progress (0.0–1.0), custom colors |
| `gradient_box` | Box with two-stop gradient (horizontal / vertical / diagonal) + corner radius |

### Smart Weather Components (auto-bind to live data)

| Type | Sizes | Description |
|---|---|---|
| `weather_card` | `large` `medium` `compact` `minimal` | Complete weather card — city, temperature, icon, condition, feels like |
| `temperature_hero` | `large` `medium` `small` | Dramatic temperature showcase (96sp / 64sp / 40sp) |
| `weather_summary` | `large` `medium` `small` | Horizontal compact summary row |
| `weather_stat` | `large` `medium` `small` | Single metric display (humidity / wind / pressure / feels\_like / visibility / cloud\_cover) |
| `weather_details` | — | 2-column grid of all stat chips |
| `condition_badge` | — | Pill with condition emoji + text |
| `humidity_bar` | — | LinearProgressIndicator for live humidity |
| `wind_display` | `large` `medium` `small` | Wind speed card / row / inline |
| `search_bar` | — | City search input; fires `search_city` action |
| `button` | — | Tappable button; fires any `actionKey` (e.g. `refresh`) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose (Material3) |
| Architecture | Clean Architecture — domain / data / presentation |
| State management | `StateFlow` + `ViewModel` |
| Networking (weather) | [Ktor](https://ktor.io) (OkHttp engine) + kotlinx-serialization |
| AI agent | [Koog AI](https://koog.ai) `0.6.3` — `koog-agents` + `prompt-executor-openai-client` |
| LLM | OpenAI GPT-4o |
| Serialization | kotlinx-serialization-json |

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/your-username/DynamicWeather.git
cd DynamicWeather
```

### 2. Get API keys

| Key | Where to get it |
|---|---|
| **OpenWeatherMap** | [openweathermap.org/api](https://openweathermap.org/api) — free tier works |
| **OpenAI** | [platform.openai.com/api-keys](https://platform.openai.com/api-keys) |

### 3. Add your keys

Open `app/src/main/java/today/thisaay/dynamicweather/ApiConstants.kt` and replace the placeholder values:

```kotlin
object ApiConstants {
    const val OPEN_WEATHER_MAP_API_KEY = "your_openweathermap_key_here"
    const val OPENAI_API_KEY = "your_openai_key_here"
    const val DEFAULT_CITY = "Cairo"
}
```

### 4. Build and run

Open the project in Android Studio and run on an emulator or device (minSdk 24).

---

## How the AI Layout Agent Works

The agent lives in `agent/LayoutAgent.kt`. It is **completely stateless** — each call:

1. Creates a fresh `AIAgent` instance
2. Sends the **full system prompt** (complete JSON schema + 7 example layouts)
3. Sends the **current layout JSON** as context + the user's natural-language request
4. Receives a new JSON string
5. The ViewModel validates it by parsing into `UIComponent` — if invalid, the current layout is kept

The system prompt (`UISchemaPrompt.kt`) documents every component type, every field, and every size variant, with example layouts that demonstrate:

- Default layout
- Gradient hero with individual stats
- Stats dashboard with large stat cards
- Box overlay with diagonal gradient
- Compact info-dense layout with chips and badges
- Minimal / ultra-compact sizes
- Progress bars and standalone icons

### Example requests you can try

> *"Make it look like a space mission dashboard, dark with glowing cyan accents"*

> *"Show only the temperature in the center of the screen, as large as possible"*

> *"Redesign it as a minimal newspaper-style layout with just text, no icons"*

> *"Create a card-heavy layout with every weather stat in its own large stat card"*

---

## Project Structure (key files)

```
ApiConstants.kt                    ← Put your API keys here
domain/model/UIComponent.kt        ← Full sealed class JSON schema (~22 component types)
domain/LayoutDefaults.kt           ← Default Cairo layout JSON string
agent/UISchemaPrompt.kt            ← AI system prompt with full schema + 7 layout examples
agent/LayoutAgent.kt               ← Koog AIAgent (stateless, GPT-4o)
presentation/viewmodel/
  WeatherViewModel.kt              ← StateFlows, onAction(), requestLayoutChange()
presentation/renderer/
  DynamicUIRenderer.kt             ← Recursive JSON → Compose engine
  HighLevelComponents.kt           ← Smart weather composables (all sizes)
  ColorUtils.kt / IconUtils.kt     ← Shared color + icon mapping utilities
presentation/screen/
  WeatherScreen.kt                 ← Root screen + FAB
  LayoutRequestDialog.kt           ← "Redesign" dialog
```

---

## License

```
Copyright 2026 Dynamic Weather Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

See the [LICENSE](LICENSE) file for the full text.