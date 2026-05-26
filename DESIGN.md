# DESIGN.md — Opportune System Design

> **Audience:** Developers, architects, and AI agents working on this codebase.
> **Purpose:** Documents the overall system design, frontend design language, architectural decisions, and conventions that hold the project together.
> This is a living document — update it when decisions change.

---

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Architecture](#2-architecture)
3. [Backend Design](#3-backend-design)
4. [Frontend Design](#4-frontend-design)
5. [UI Design Language](#5-ui-design-language)
6. [State Management](#6-state-management)
7. [Data Flow](#7-data-flow)
8. [Key Design Decisions](#8-key-design-decisions)
9. [Folder Structure Reference](#9-folder-structure-reference)

---

## 1. System Overview

**Opportune** is a web application that helps job seekers craft tailored resumes and cover letters using AI assistance. Users authenticate via OAuth2, manage their professional profile, and generate AI-refined documents targeted to specific job postings.

```
┌─────────────────────────────────────────────┐
│                  Browser                    │
│   Vue 3 + Vuetify (Tabler-styled) SPA       │
└─────────────────┬───────────────────────────┘
                  │ REST / JSON  (JWT Bearer)
┌─────────────────▼───────────────────────────┐
│          Spring Boot 4 API Server           │
│  Controllers → Services → Repositories      │
│  Spring AI ── OpenAI API                    │
└─────────────────┬───────────────────────────┘
                  │ JPA / Flyway
┌─────────────────▼───────────────────────────┐
│              PostgreSQL                     │
└─────────────────────────────────────────────┘
```

The frontend is a fully separate single-page application (SPA) bundled by Vite and served as static resources from within the Spring Boot JAR (via `node-gradle` integration in `build.gradle.kts`).

---

## 2. Architecture

### Monorepo with Integrated Build

Both the backend (Kotlin/Spring Boot) and the frontend (Vue 3) live in the same repository and are built together via Gradle. The frontend build is delegated to npm through the `node-gradle` plugin; its output is placed in `src/main/resources/static/` so Spring Boot serves it directly.

```
build.gradle.kts
│
├── :backend tasks   → compile Kotlin, run tests, assemble JAR
└── :frontend tasks  → npm install → vite build → copy to static/
```

This means a single `./gradlew bootJar` produces a self-contained, deployable artifact with no separate web server needed.

### Layering (Backend)

```
HTTP Request
     │
     ▼
Controller  (REST endpoints, request/response mapping)
     │
     ▼
Service     (business logic, AI orchestration, PDF/text processing)
     │
     ▼
Repository  (Spring Data JPA — DB access only)
     │
     ▼
PostgreSQL
```

No business logic lives in Controllers or Repositories. AI calls (Spring AI) are made from the Service layer.

### Layering (Frontend)

```
View (.vue)
     │
     ▼
Composable  (reusable reactive logic, useXxx pattern)
     │
     ▼
Store (Pinia)   ←──────  Service (Axios API calls)
     │
     ▼
Models (TypeScript interfaces — pure data shapes)
```

Views are thin. Business/fetching logic lives in Composables and Stores, not in `<script setup>` blocks directly.

---

## 3. Backend Design

### Technology Stack

| Concern          | Technology                                     |
| ---------------- | ---------------------------------------------- |
| Language         | Kotlin                                         |
| Framework        | Spring Boot 4                                  |
| AI integration   | Spring AI (OpenAI adapter)                     |
| Security         | Spring Security — OAuth2 Resource Server (JWT) |
| Persistence      | Spring Data JPA + Hibernate                    |
| DB migrations    | Flyway                                         |
| Database         | PostgreSQL                                     |
| Document parsing | Apache PDFBox (PDF), Jsoup (HTML/web scraping) |
| Build            | Gradle (Kotlin DSL)                            |

### Security Model

The API is a stateless **OAuth2 Resource Server**. Every request must carry a `Bearer` JWT in the `Authorization` header. The JWT is validated against the configured issuer. There is no session state on the server — all user identity is derived from the JWT claims on each request.

### AI Integration

Spring AI is configured with an OpenAI-compatible provider. Prompt templates are stored as `.st` (StringTemplate) files under `src/main/resources/prompts/`. The `ChatClient` bean is injected into service classes that require AI capabilities. See [`AGENTS.md`](./AGENTS.md) for the full catalogue of AI agents and their prompt files.

### Database Migrations

All schema changes are managed by Flyway. Migration scripts live in `src/main/resources/db/migration/` and follow the `V{version}__{description}.sql` naming convention. **Never alter an already-applied migration** — always create a new one.

---

## 4. Frontend Design

### Technology Stack

| Concern                | Technology                                    |
| ---------------------- | --------------------------------------------- |
| Language               | TypeScript                                    |
| Framework              | Vue 3 (Composition API, `<script setup>`)     |
| UI component library   | Vuetify 4                                     |
| Visual design language | Tabler (via overrides — see §5)               |
| State management       | Pinia                                         |
| Routing                | Vue Router                                    |
| HTTP client            | Axios (wrapped in `/services`)                |
| Markdown editor        | md-editor-v3                                  |
| Markdown rendering     | marked                                        |
| Charts                 | Apache ECharts (via vue-echarts, tree-shaken) |
| Build tool             | Vite                                          |
| Icons                  | Material Design Icons (`@mdi/font`)           |

### Style Loading Order

The order in `main.ts` is intentional and **must be preserved**:

```ts
import 'vuetify/styles' // 1. Vuetify base — lowest specificity
import '@mdi/font/css/materialdesignicons.css' // 2. Icon font
import './assets/main.scss' // 3. App styles (includes tabler-overrides.scss)
```

`tabler-overrides.scss` is imported from within `main.scss` and wins over Vuetify base styles by loading last. Never reorder these imports.

### Frontend Directory Layout

```
src/main/webapp/src/
├── assets/
│   ├── main.scss              # Entry SCSS — imports tabler-overrides.scss
│   └── tabler-overrides.scss  # Tabler design language applied to Vuetify components
├── components/                # Reusable, dumb UI components
├── composables/               # Shared reactive logic (useXxx)
├── layouts/                   # Page layout shells (AppLayout, AuthLayout, etc.)
├── models/                    # TypeScript interfaces — pure data shapes, no logic
├── plugins/
│   └── vuetify.ts             # Vuetify plugin config (theme, defaults)
├── router/                    # Vue Router routes definition
├── services/                  # Axios API call wrappers — one file per domain
├── stores/                    # Pinia stores — one file per domain
├── utils/                     # Pure helper functions (no Vue reactivity)
├── views/                     # Page-level components (route targets)
├── App.vue                    # Root component
└── main.ts                    # Application entry point
```

---

## 5. UI Design Language

### Principle: Vuetify Components, Tabler Aesthetics

**Rule:** Prefer Vuetify components wherever they exist. Plain HTML elements are not forbidden — use them when Vuetify has no equivalent or when a lightweight element is more appropriate — but do not replace existing Vuetify components with raw HTML counterparts just to avoid the component API. Style customisation is done through `tabler-overrides.scss`.

The reason: Vuetify handles accessibility, keyboard navigation, ARIA roles, and consistent theming out of the box. Tabler's design language is applied on top — not instead of — Vuetify's component system.

### What Tabler Brings

[Tabler](https://tabler.io/) is a clean, flat, professional admin UI toolkit. Its key visual traits — and how they are applied here — are:

| Tabler Trait                     | Implementation in Opportune                                                         |
| -------------------------------- | ----------------------------------------------------------------------------------- |
| Flat surfaces, minimal elevation | All `.v-sheet` elevations capped to `box-shadow: 0 1px 3px rgba(0,0,0,0.05)`        |
| Subtle border-based separation   | Cards, nav drawer, app bar, dividers use `rgba(101,109,119,0.14)` borders           |
| No ALL-CAPS buttons              | `.v-btn` override: `text-transform: none`, `letter-spacing: 0`                      |
| Compact, clean tables            | Table headers uppercase, `0.75rem`, `font-weight: 600`; row borders at `0.10` alpha |
| `Inter` as the system font       | `.v-application` overrides to `Inter, sans-serif` at `15px / 1.5`                   |
| `4px` button border-radius       | `.v-btn`: `border-radius: 4px`                                                      |
| `8px` card border-radius         | `.v-card`: `border-radius: 8px`                                                     |
| 44px minimum touch targets       | List items and link items enforced to `min-height: 44px`                            |
| High-contrast tooltips           | Tooltip overlay uses inverted `on-surface / surface` colour pair                    |

### Design Tokens

Vuetify's theme system is configured in `src/main/webapp/src/plugins/vuetify.ts`. `tabler-overrides.scss` uses `var(--v-theme-*)` custom properties to stay theme-aware and does **not** hardcode semantic colour hex values — the only fixed colour is the structural neutral grey used for borders and dividers.

#### Colour — `tablerLight` (default)

| Token             | Value     | Usage                             |
| ----------------- | --------- | --------------------------------- |
| `primary`         | `#0054a6` | Buttons, links, active states     |
| `secondary`       | `#616876` | Secondary actions, muted text     |
| `success`         | `#2fb344` | Success badges, confirmations     |
| `warning`         | `#f76707` | Warning alerts                    |
| `error`           | `#d63939` | Error states, destructive actions |
| `info`            | `#4299e1` | Info banners                      |
| `background`      | `#f4f6fb` | Page background                   |
| `surface`         | `#ffffff` | Cards, dialogs, drawers           |
| `surface-variant` | `#f1f5f9` | Chips, secondary surfaces         |
| `on-background`   | `#1d273b` | Body text on background           |
| `on-surface`      | `#1d273b` | Body text on surface              |

#### Colour — `tablerDark`

| Token             | Value     | Usage                             |
| ----------------- | --------- | --------------------------------- |
| `primary`         | `#4299e1` | Buttons, links, active states     |
| `secondary`       | `#858d9b` | Secondary actions, muted text     |
| `success`         | `#47c272` | Success badges, confirmations     |
| `warning`         | `#fd9c4e` | Warning alerts                    |
| `error`           | `#e35d6a` | Error states, destructive actions |
| `info`            | `#79b8f3` | Info banners                      |
| `background`      | `#1a1f2e` | Page background                   |
| `surface`         | `#222736` | Cards, dialogs, drawers           |
| `surface-variant` | `#2a2f3e` | Chips, secondary surfaces         |
| `on-background`   | `#c8d3e1` | Body text on background           |
| `on-surface`      | `#c8d3e1` | Body text on surface              |

#### Structural neutral (fixed — not theme-aware)

| Usage                                    | Value                        |
| ---------------------------------------- | ---------------------------- |
| Card / drawer / app bar / divider border | `rgba(101, 109, 119, 0.14)`  |
| Table row separator                      | `rgba(101, 109, 119, 0.10)`  |
| Table header text                        | `rgba(101, 109, 119, 0.80)`  |
| Flat surface shadow                      | `0 1px 3px rgba(0,0,0,0.05)` |

#### Typography

| Property                           | Value                                                                       |
| ---------------------------------- | --------------------------------------------------------------------------- |
| Font family                        | `Inter` (loaded from Google Fonts), fallback: system-ui stack               |
| Font weights                       | 300, 400, 500, 600, 700 (loaded)                                            |
| Base size                          | `15px`                                                                      |
| Line height                        | `1.5`                                                                       |
| Letter spacing                     | `0` (reset — no default tracking)                                           |
| Antialiasing                       | `-webkit-font-smoothing: antialiased`; `-moz-osx-font-smoothing: grayscale` |
| Responsive h1 / `.text-h4`         | `clamp(1.2rem, 3vw, 1.75rem)`                                               |
| Responsive `.text-h5` / `.text-h6` | `clamp(0.85rem, 2vw, 1rem)`                                                 |
| Card title                         | `clamp(0.875rem, 2vw, 1rem)`                                                |
| Tooltip text                       | `0.8125rem`                                                                 |
| Table header                       | `0.75rem`, `font-weight: 600`, uppercase, `letter-spacing: 0.04em`          |

#### Shape & spacing

| Token / component                    | Value  |
| ------------------------------------ | ------ |
| Global border-radius                 | `4px`  |
| Card border-radius                   | `8px`  |
| Button border-radius                 | `4px`  |
| Input field border-radius            | `4px`  |
| List item border-radius              | `4px`  |
| Button min-height                    | `40px` |
| Icon button min-width                | `40px` |
| Touch target min-height (list items) | `44px` |

### Theming & Dark / Light Mode

The app ships two named Vuetify themes — `tablerLight` (default) and `tablerDark` — both defined in `plugins/vuetify.ts`.

**How switching works:**

- The toggle button lives in the app bar (`AppBar.vue`). It calls `toggleTheme()` in `AppBar.ts`, which flips `vuetifyTheme.global.name.value` between the two theme names:
  ```ts
  vuetifyTheme.global.name.value =
    vuetifyTheme.global.name.value === 'tablerLight' ? 'tablerDark' : 'tablerLight'
  ```
- Vuetify's `useTheme()` composable is used directly — there is no Pinia store for theme state.
- `tabler-overrides.scss` uses `var(--v-theme-*)` CSS custom properties throughout, so it responds to theme changes automatically without any extra code.
- **Preference is not persisted** — the theme resets to `tablerLight` on page reload.

**Third-party component sync:**

Components outside the Vuetify system must be synced manually. The pattern is to read `vuetifyTheme.global.current.value.dark` and derive the component's own theme string:

```ts
// MdEditor.vue
const vuetifyTheme = useTheme()
const editorTheme = computed(() => (vuetifyTheme.global.current.value.dark ? 'dark' : 'light'))
```

Apply the same pattern to any future third-party component that has its own dark/light prop.

**Rules:**

- Never hardcode `background`, `surface`, or text colours using hex values in components — always use `var(--v-theme-*)` or Vuetify's colour utility classes so the component works in both themes.
- The structural neutral `rgba(101, 109, 119, …)` borders in `tabler-overrides.scss` are the only intentionally theme-fixed values; they are visually appropriate at both light and dark luminance levels.

### Adding or Modifying Styles

- **Component-scoped styles** → `<style scoped>` inside the `.vue` file.
- **Global Vuetify overrides** (affecting all instances of a component) → `tabler-overrides.scss`.
- **New design tokens / CSS custom properties** → `plugins/vuetify.ts` theme object.
- **Never** add global overrides inside `<style>` without `scoped` unless they truly need to be global, and never duplicate rules already in `tabler-overrides.scss`.

### ECharts Integration

ECharts is registered globally in `main.ts` as `<VChart>`. Only the chart types and components actually used are imported (`tree-shaken`) to keep the bundle lean. When adding a new chart type, import and register it in `main.ts` — do not import it inside individual components.

Chart wrapper elements should carry the `.chart-wrapper` class or a `chart-*` BEM class to pick up the `tabler-overrides.scss` rule that enforces `width: 100%; aspect-ratio: 16/9; overflow: hidden`.

### User Feedback (Toast Notifications)

Every async operation that can succeed or fail must give the user visible feedback. Silent failures are not acceptable.

**Principle:** Use a single global toast/snackbar system — one `<v-snackbar>` mounted in `App.vue`, driven by a Pinia store (`useToastStore` in `stores/toast.ts`). Components call the store; they do not own snackbar state themselves.

```ts
// Correct — call the global store
const toast = useToastStore()
try {
  await someApiCall()
  toast.success('Saved successfully')
} catch {
  toast.error('Something went wrong. Please try again.')
}
```

**Rules:**

- **Always** show a toast on API error — never swallow exceptions silently.
- **Always** confirm destructive or long-running actions with a success toast on completion.
- Use `toast.success()` / `toast.error()` / `toast.warning()` / `toast.info()` — map to Vuetify's `success`, `error`, `warning`, `info` colours respectively.
- **Never** add a `<v-snackbar>` or local snackbar reactive state inside a component.

### Markdown

- **Editing** (user-facing input): use `md-editor-v3`. Its theme should match the active Vuetify theme.
- **Rendering** (display-only output): use `marked`. Strip unsafe HTML via a sanitiser before inserting rendered output into the DOM.

---

## 6. State Management

Pinia is the single source of truth for application state. Stores are organised by domain (one file per feature area). The conventions are:

- **Stores** manage server-fetched data, loading/error states, and actions that call services.
- **Services** (`/services/`) contain only Axios calls — no reactive state.
- **Composables** (`/composables/`) contain reusable local reactive logic that does not need to be globally shared.
- Do not put API call logic directly into Pinia actions — delegate to a service function and call it from the action.

---

## 7. Data Flow

### Authenticated Request Flow

```
User action in View
       │
       ▼
Composable / Store action
       │
       ▼
Service (Axios) ──► adds JWT Bearer header via Axios interceptor
       │
       ▼
Spring Boot REST Controller
       │  validates JWT via Spring Security
       ▼
Service layer (business logic / AI call if needed)
       │
       ▼
Repository → PostgreSQL
       │
       ◄── response back up the chain
       ▼
Store state updated → View re-renders reactively
```

### AI-Assisted Document Generation Flow

```
User submits job description + profile data
       │
       ▼
Frontend POST /api/ai/{feature}
       │
       ▼
Spring AI Service
       │  fills prompt template (.st file)
       ▼
OpenAI API (streaming or blocking)
       │
       ▼
Formatted Markdown response
       │
       ▼
Frontend renders via `marked` in md-editor-v3 preview
```

---

## 8. Key Design Decisions

### Why a monorepo with integrated Gradle build?

A single `./gradlew bootJar` produces a deployment-ready JAR with no separate frontend pipeline to orchestrate in CI. The trade-off is a slightly more complex `build.gradle.kts`, but this was preferred over managing two separate deployment units for a project of this scale.

### Why Vuetify + Tabler overrides instead of Tabler directly?

Tabler is a CSS/HTML framework without a native Vue component system. Vuetify provides a mature, accessible Vue 3 component library. The Tabler override approach gets Tabler's visual quality while keeping Vuetify's accessibility, keyboard navigation, and component API intact. The override file (`tabler-overrides.scss`) is the single place where this translation lives — making it auditable and reversible.

### Why ECharts over Chart.js or Recharts?

ECharts handles complex chart types (radar, heatmap, funnel, calendar) that are needed for resume analytics features and is well-supported in the Vue ecosystem via `vue-echarts`. It is tree-shaken in `main.ts` to avoid importing the full ~1MB bundle.

### Why Flyway over Liquibase?

Flyway's SQL-first migration model means migrations are plain `.sql` files — readable by anyone without learning a DSL. For a project with PostgreSQL and a relatively stable schema, Flyway is a lower-overhead choice.

### Why stateless JWT (no server sessions)?

Stateless auth simplifies horizontal scaling and removes the need for a distributed session store. The OAuth2 Resource Server model with JWT aligns with modern SPA + API patterns and is idiomatic in Spring Security.

---

## 9. Folder Structure Reference

```
opportune/
├── build.gradle.kts                   # Unified build: backend + frontend
├── AGENTS.md                          # AI agent catalogue
├── DESIGN.md                          # This file
│
├── src/main/
│   ├── kotlin/                        # Backend source
│   │   └── …/opportune/
│   │       ├── aop/                   # Cross-cutting concerns (logging, auditing aspects)
│   │       ├── config/                # Spring configuration beans (Security, Jackson, OpenAPI, etc.)
│   │       ├── dto/                   # Data Transfer Objects (request/response shapes)
│   │       ├── entity/                # JPA entities
│   │       ├── enums/                 # Shared enumerations (Role, ApplicationStatus, etc.)
│   │       ├── event/                 # Spring application events
│   │       ├── mapper/                # MapStruct mappers (entity ↔ DTO)
│   │       ├── projection/            # JPA interface projections for lightweight queries
│   │       ├── repository/            # Spring Data JPA repositories
│   │       ├── security/              # JWT utilities, SecurityUtils, auth converters
│   │       ├── service/               # Business logic (interfaces + impl/ sub-package)
│   │       ├── util/                  # Shared utilities
│   │       └── web/                   # REST controllers, filters, exception handlers
│   │
│   ├── resources/
│   │   ├── application.yml            # Base configuration
│   │   ├── application-*.yml          # Profile-specific overrides
│   │   ├── db/migration/              # Flyway SQL migrations (V{n}__{desc}.sql)
│   │   └── prompts/                   # Spring AI prompt templates (.st files)
│   │
│   └── webapp/                        # Frontend root (Vite project)
│       ├── package.json
│       ├── vite.config.ts
│       └── src/
│           ├── assets/
│           │   ├── main.scss
│           │   └── tabler-overrides.scss   # ← Tabler design language
│           ├── components/
│           ├── composables/
│           ├── layouts/
│           ├── models/
│           ├── plugins/vuetify.ts          # ← Vuetify theme config
│           ├── router/
│           ├── services/
│           ├── stores/
│           ├── utils/
│           ├── views/
│           ├── App.vue
│           └── main.ts                    # ← Style loading order defined here
│
└── src/test/                          # Backend tests (JUnit 5)
```

---

_Last updated: May 2026_
_Maintainer: see `CONTRIBUTING.md`_
