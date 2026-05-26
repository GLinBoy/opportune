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
├── package.json
├── AGENTS.md
├── DESIGN.md
├── ARCHITECTURE.md
├── CONTRIBUTING.md
├── DEVELOPMENT.md
└── src/
    └── main/
        ├── kotlin/
        ├── resources/
        └── webapp/
            └── src/
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
├── aop/             # Cross-cutting concerns (logging, auditing aspects)
├── config/          # Spring configuration beans (Security, Jackson, OpenAPI, etc.)
├── dto/             # Data Transfer Objects (request/response shapes)
├── entity/          # JPA entities
├── enums/           # Shared enumerations (Role, ApplicationStatus, etc.)
├── event/           # Spring application events (e.g. ApplicationSubmittedEvent)
├── mapper/          # MapStruct mappers (entity ↔ DTO)
├── projection/      # JPA interface projections for lightweight queries
├── repository/      # Spring Data JPA repositories
├── security/        # JWT utilities, SecurityUtils, auth converters
├── service/         # Business logic and orchestration (interfaces + impl/ sub-package)
├── util/            # Shared utilities
└── web/             # REST controllers, filters, exception handlers
    ├── controller/
    ├── filter/
    └── rest/
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

### Component strategy

The UI component layer should be built with Vuetify components, while the visual language should follow a Tabler-inspired style system applied through shared overrides rather than bypassing Vuetify.

This means:

- use Vuetify as the component foundation
- style globally through shared overrides where appropriate
- avoid replacing Vuetify components with raw HTML just to imitate Tabler

### Global style architecture

`src/main/webapp/src/assets/tabler-overrides.scss` contains global visual overrides for cards, buttons, fields, drawers, tables, dividers, elevations, app bars, list items, typography, touch targets, tooltips, images, and chart wrappers.

Use style locations deliberately:

- local component styles -> inside the component
- global Vuetify visual overrides -> `src/main/webapp/src/assets/tabler-overrides.scss`
- global app stylesheet entry -> `src/main/webapp/src/assets/main.scss`
- theme configuration -> `src/main/webapp/src/plugins/vuetify.ts`

### Style loading order

The application entrypoint (`src/main/webapp/src/main.ts`) imports styles in this order, which must be preserved so project styles can override library defaults predictably:

1. `vuetify/styles` — Vuetify base styles
2. `@mdi/font/css/materialdesignicons.css` — MDI icons
3. `./assets/main.scss` — app typography and Tabler overrides

### Chart architecture

ECharts is registered globally in `src/main/webapp/src/main.ts` through `vue-echarts`. Only the needed renderers, charts, and components are imported to keep the bundle lean (`CanvasRenderer`, `PieChart`, `LineChart`, `BarChart`, `RadarChart`, `FunnelChart`, `HeatmapChart`, and related ECharts components). A global `VChart` component is registered for use in views.

Architecturally, that means:

- chart bootstrapping belongs in application setup
- chart rendering belongs in components/views
- new chart types should be added to the shared registration path, not bootstrapped ad hoc in random components

### Markdown architecture

Markdown editing and rendering should be treated as first-class application capabilities.

Recommended separation:

- editing layer -> editor component integration
- rendering layer -> safe markdown rendering
- persistence layer -> backend-stored or backend-generated markdown
- AI layer -> markdown generation or transformation workflows

Keep markdown human-readable first, even when it is AI-generated.

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

- `AGENTS.md`
- `DESIGN.md`
- `CONTRIBUTING.md`
- `DEVELOPMENT.md`

Future linked docs:

- `API.md`
- `DATA_MODEL.md`
- `SECURITY.md`
- `PROMPTS.md`
- `AI_CONTEXT.md`
- `TESTING.md`
