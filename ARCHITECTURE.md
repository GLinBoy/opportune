# ARCHITECTURE.md

This document describes the structural architecture of Opportune.

Its goal is to help contributors, maintainers, and AI tools understand how the application is organized across backend and frontend, how major responsibilities are separated, and where new code should be added.

This file focuses on structure. For broader reasoning and design philosophy, see `DESIGN.md`. For AI-specific behavior, see `AGENTS.md`. For day-to-day contributor workflow, see `CONTRIBUTING.md` and `DEVELOPMENT.md`.

---

## Purpose

Opportune is built as a single repository that contains both server-side and client-side code.

The architecture aims to:

- keep backend and frontend responsibilities clear
- keep deployment simple
- reduce duplication
- preserve a consistent design language
- make the codebase understandable for both humans and AI systems

---

## Architectural Style

At a high level, the project follows a layered application architecture on the backend and a feature-oriented SPA architecture on the frontend.

### Backend style

The backend should follow a layered flow:

```text
HTTP/API
  -> Controller
  -> Service
  -> Repository
  -> Database
```

Each layer has a clear responsibility:

- **Controller**: request mapping, validation boundary, HTTP status handling, DTO mapping
- **Service**: business logic, orchestration, AI integration, parsing, workflows
- **Repository**: persistence only
- **Database**: durable storage and schema-managed data

### Frontend style

The frontend follows a view-driven SPA architecture:

```text
View
  -> Composable / Store
  -> Service
  -> API
```

Each frontend layer has a clear responsibility:

- **View**: route-level UI
- **Component**: reusable presentation building block
- **Composable**: shared reactive logic
- **Store**: shared application state
- **Service**: HTTP communication and external calls
- **Model**: TypeScript data shape
- **Utility**: pure helper logic

---

## Repository Structure

The repository should be understood in two main parts:

- backend application code
- frontend application code

A simplified layout looks like this:

```text
opportune/
├── build.gradle.kts
├── settings.gradle.kts
├── package.json
├── compose.yaml
├── Dockerfile
├── Makefile
├── docs/
├── AGENTS.md
├── DESIGN.md
├── ARCHITECTURE.md
├── CONTRIBUTING.md
├── DEVELOPMENT.md
└── src/
    ├── main/
    │   ├── kotlin/          → Backend source (Kotlin + Spring Boot)
    │   ├── resources/       → config/, db/migration/, prompts/, templates/
    │   └── webapp/          → Frontend source (Vue 3 + Vite)
    │       └── src/
    └── test/
        ├── kotlin/          → Backend tests (JUnit 5)
        └── resources/       → Test config (application-test.yml)
```

The frontend source folders are `assets`, `components`, `composables`, `layouts`, `models`, `plugins`, `router`, `services`, `stores`, `utils`, and `views` under `src/main/webapp/src/`.

---

## Backend Architecture

### Backend responsibilities

The backend owns:

- business rules
- authentication and authorization enforcement
- persistence
- AI orchestration
- document processing
- API contracts consumed by the frontend

The backend should remain the source of truth for domain behavior.

### Backend package structure

The actual package layout is:

```text
src/main/kotlin/.../opportune/
├── aop/
│   ├── logging/         # Logging aspects
│   └── session/         # Session tracking aspects
├── config/              # Spring configuration beans (Security, Jackson, OpenAPI, etc.)
├── dto/                 # Data Transfer Objects (request/response shapes)
├── entity/              # JPA entities
├── enums/               # Shared enumerations (Role, ApplicationStatus, etc.)
├── event/               # Spring application events (e.g. ApplicationSubmittedEvent)
├── mapper/              # MapStruct mappers (entity ↔ DTO)
├── projection/          # JPA interface projections for lightweight queries
├── repository/          # Spring Data JPA repositories
├── security/
│   └── jwt/             # JWT utilities, SecurityUtils, auth converters
├── service/
│   └── impl/            # Business logic and orchestration
├── util/                # Shared utilities
└── web/
    ├── controller/      # REST controllers
    ├── filter/          # Servlet filters
    └── rest/            # REST exception handlers / advice
```

AI service logic lives in `service/` — there is no separate `ai/` package. Keep new backend code in the most specific existing package that matches its responsibility.

### Backend layering rules

Follow these rules consistently:

- Controllers must not contain business logic.
- Repositories must not contain business logic.
- Services may call repositories, AI clients, and other services.
- Controllers should return DTOs, not raw persistence entities, unless intentionally designed otherwise.
- Parsing, AI prompting, and workflow orchestration belong in services or dedicated support packages.

### Backend data flow

A normal request should follow this path:

```text
Client request
  -> Controller
  -> Service
  -> Repository
  -> Database
  -> Service
  -> Controller
  -> Client response
```

An AI-assisted request should follow this path:

```text
Client request
  -> Controller
  -> Service
  -> Prompt preparation / AI adapter
  -> AI provider
  -> Service post-processing
  -> Controller response
```

### Persistence and migrations

Database structure should be versioned through migrations, and schema evolution should be additive.

Keep these rules:

- create new migrations instead of editing old ones
- keep entity logic minimal
- keep repositories focused on persistence concerns
- prefer explicit domain naming in tables, fields, and DTOs

---

## Frontend Architecture

### Frontend responsibilities

The frontend owns:

- user interaction
- routing
- state presentation
- client-side form handling
- rendering markdown and interactive UI
- calling backend APIs

The frontend should not reimplement backend business rules unless there is a clear UX reason.

### Frontend structure

The current frontend source tree contains these top-level folders inside `src/main/webapp/src/`: `assets`, `components`, `composables`, `layouts`, `models`, `plugins`, `router`, `services`, `stores`, `utils`, and `views`.

### Frontend folder responsibilities

Use the existing folders like this:

- **assets/**: global styles, SCSS, static frontend assets
- **components/**: reusable UI components
- **composables/**: reusable reactive logic with `useXxx` patterns
- **layouts/**: layout shells and page chrome
- **models/**: TypeScript interfaces and data types
- **plugins/**: framework/plugin configuration such as Vuetify
- **router/**: route definitions and navigation structure
- **services/**: API wrappers and external client logic
- **stores/**: Pinia stores for shared state
- **utils/**: pure helper functions
- **views/**: route-target pages

### Frontend flow

The preferred flow is:

```text
User action
  -> View
  -> Composable or Store
  -> Service
  -> Backend API
  -> Store update / reactive state change
  -> UI rerender
```

### Frontend composition rules

Keep these rules:

- Views should stay thin.
- Reusable UI belongs in `components/`.
- Shared state belongs in `stores/`.
- API calls belong in `services/`.
- Local reusable behavior belongs in `composables/`.
- Pure transformations belong in `utils/`.

If a view grows too much, split it before continuing to add logic.

---

## UI Architecture

The UI architecture is built on three layers — see [`DESIGN.md §5`](./DESIGN.md) for the full design language and tokens.

### Component strategy

Vuetify components are the foundation; a Tabler-inspired visual language is applied through shared overrides in `tabler-overrides.scss`. Never bypass Vuetify components for raw HTML just to match Tabler aesthetics.

### Style loading order

The entrypoint (`src/main/webapp/src/main.ts`) loads styles in this order (must be preserved):

1. `vuetify/styles` — Vuetify base styles
2. `@mdi/font/css/materialdesignicons.css` — MDI icons
3. `./assets/main.scss` — app styles (includes Tabler overrides)

### Global style locations

- Component-scoped styles → `<style scoped>` inside `.vue` files
- Global Vuetify overrides → `tabler-overrides.scss`
- Theme configuration → `plugins/vuetify.ts`

### Chart architecture

ECharts is registered globally in `main.ts` via `vue-echarts` with only needed chart types imported (tree-shaken). New chart types must be added to the shared registration path in `main.ts`, not bootstrapped ad hoc in components. Chart wrappers use `.chart-wrapper` class.

### Markdown architecture

Markdown is a first-class content format. The architecture separates editing (`md-editor-v3`), rendering (`marked` + sanitisation), persistence (backend), and generation (AI). See [`DESIGN.md §5`](./DESIGN.md) for the full markdown conventions.

---

## Integration Points

### Backend to frontend

The frontend should consume backend functionality only through explicit API contracts.

Keep these rules:

- do not couple frontend logic to internal backend implementation details
- do not encode server assumptions in many scattered places
- centralize HTTP calls in `services/`
- keep request/response models explicit

### AI integration point

AI functionality should be treated as an application service, not as random helper logic.

Architecturally, AI flows should pass through:

- explicit backend services
- prompt templates and agent definitions
- documented contracts for input and output
- post-processing and validation before the frontend renders output

Cross-reference any AI behavior in `AGENTS.md`.

---

## Build and Delivery View

The repository is organized so backend and frontend live together, while the frontend source itself is nested under `src/main/webapp/src/`.

From an architectural point of view, this gives you:

- one repository for the full application
- shared documentation at the root
- a clean separation between server runtime code and SPA client code
- a place to document both layers together, which is important for contributors and AI tools

---

## Cross-Cutting Concerns

### Security

Security belongs primarily to the backend architecture, but the frontend must respect it through correct token handling, guarded routes, and safe API usage.

Security-related architectural changes should also update `SECURITY.md`.

### Documentation

Documentation is part of the architecture, not outside it.

Root-level markdown files should be used as shared project memory for:

- contributors
- reviewers
- maintainers
- AI systems

This repository is especially suited to an Obsidian-style documentation vault, because the docs can become a linked knowledge graph of architecture, design, AI behavior, API shape, and workflows.

### Observability and error handling

Keep error handling intentional:

- frontend should show actionable user-facing errors
- backend should log operationally useful information
- unexpected failures should be easy to trace across layers

---

## Change Guidelines

When changing architecture:

- preserve layer boundaries
- update the relevant root markdown files
- prefer extending an existing pattern over introducing a parallel one
- document the reason for the change, not just the result

Architecture changes usually require updates to more than one file.

Typical examples:

- AI behavior change -> `AGENTS.md` and possibly `DESIGN.md`
- folder or layering change -> `ARCHITECTURE.md` and `CONTRIBUTING.md`
- local workflow impact -> `DEVELOPMENT.md`
- security boundary change -> future `SECURITY.md`

---

## Architecture Checklist

Before merging a structural change, confirm:

- [ ] Backend and frontend responsibilities are still clear.
- [ ] Controllers, services, and repositories remain properly separated.
- [ ] Views are still thin enough.
- [ ] Shared frontend logic is in stores/composables/services, not scattered in views.
- [ ] Global visual rules still live in the shared style system.
- [ ] Documentation was updated with the architectural reason for the change.

---

## Related Docs

- [`AGENTS.md`](./AGENTS.md) — AI agent catalogue and prompt architecture
- [`DESIGN.md`](./DESIGN.md) — system design, UI language, design decisions
- [`CONTRIBUTING.md`](./CONTRIBUTING.md) — contribution workflow and rules
- [`DEVELOPMENT.md`](./DEVELOPMENT.md) — local setup and daily routine
- [`API.md`](./API.md) — REST API contract
- [`DATA_MODEL.md`](./DATA_MODEL.md) — domain entities and JPA conventions
- [`SECURITY.md`](./SECURITY.md) — authentication and authorization model
- [`PROMPTS.md`](./PROMPTS.md) — prompt template documentation
- [`AI_CONTEXT.md`](./AI_CONTEXT.md) — compact project context for AI tools
- [`TESTING.md`](./TESTING.md) — testing strategy and conventions
