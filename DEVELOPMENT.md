# DEVELOPMENT.md

This document explains how to work on Opportune locally.

It is written for day-to-day contributors who need to run the app, make changes safely, and keep code, styling, and documentation aligned.

---

## Purpose

Use this file as the operational guide for local development.

It complements:

- `AGENTS.md` for AI-related behavior and prompt architecture
- `DESIGN.md` for system structure and design decisions
- `CONTRIBUTING.md` for workflow and contribution rules

---

## Project Shape

Opportune is a single repository that contains:

- a Kotlin / Spring Boot backend
- a Vue 3 frontend
- shared project documentation in the repository root

The frontend source lives under:

```text
src/main/webapp/src/
```

The frontend is organized into focused folders such as:

```text
assets/
components/
composables/
layouts/
models/
plugins/
router/
services/
stores/
utils/
views/
```

Keep new files in the most specific folder that matches their responsibility.

---

## Before You Start

Make sure you have these installed locally:

- JDK 24 (managed by Gradle toolchain — downloaded automatically if not present)
- Node.js 24.x (managed by `node-gradle` plugin — downloaded automatically)
- Git
- PostgreSQL 14+ (only required for the `prod` profile; dev uses H2 in-memory)
- An IDE suited to the stack, for example IntelliJ IDEA and/or VS Code

You should also have:

- access to required environment variables
- access to any OAuth / OpenAI-related credentials used in local development

Store secrets outside committed files.

---

## First-Time Setup

### 1. Clone the repository

```bash
git clone git@github.com:GLinBoy/opportune.git
cd opportune
```

### 2. Install backend and frontend dependencies

Backend dependencies are managed by Gradle.

Frontend dependencies are managed by npm. Run npm commands from the directory that owns the frontend `package.json`. If your local setup uses the repository root for npm, keep using that. If it uses `src/main/webapp/`, run the commands there.

Typical commands:

```bash
./gradlew build
npm install
```

### 3. Configure local environment

The application loads environment variables from `.env.<profile>` files automatically via the `loadEnvFile()` helper in `build.gradle.kts`. Copy the example file and fill in the required values:

```bash
cp .env.dev.example .env.dev   # adjust values as needed
```

Required variables for the `dev` profile:

| Variable                                    | Required | Description                             |
| ------------------------------------------- | -------- | --------------------------------------- |
| `SPRING_AI_OPENAI_API_KEY`                  | Yes      | OpenAI secret key — never commit this   |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL`       | No       | Override model (default: `gpt-4o-mini`) |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_TEMPERATURE` | No       | Override temperature (default: `0.7`)   |

Profile env files (`.env.dev`, `.env.prod`) are git-ignored. Never hardcode secrets in `application.yml` or source files.

---

## Running the App

### Docker Compose (backing services)

Start PostgreSQL and any other backing services before running with the `prod` profile:

```bash
docker compose up -d
```

The application itself is run via Gradle, not Docker, during development.

### Backend

Run the Spring Boot application using Gradle or your IDE run configuration. The `dev` profile uses an H2 in-memory database, so no external database is needed for local development.

```bash
# Start with dev profile (H2 in-memory database — default)
./gradlew bootRun -Pprofile=dev

# Start with prod profile (PostgreSQL — requires compose.yaml services)
./gradlew bootRun -Pprofile=prod
```

### Frontend

Run the frontend dev server from the repository root:

```bash
npm run dev   # Vite dev server with HMR at http://localhost:5173
```

### Make targets (recommended for full-stack development)

The `Makefile` provides convenient targets that auto-detect `tmux`, `screen`, or a fallback PID-based process manager:

```bash
make start      # start both backend and frontend
make stop       # stop both
make start-be   # backend only
make start-fe   # frontend only
make restart-be # restart backend
make restart-fe # restart frontend
make attach-be  # view backend logs
make attach-fe  # view frontend logs
make status     # show running/stopped services
make help       # list all targets
```

### Full-stack development

In normal local development:

- run the backend in one terminal (or via `make start-be`)
- run the frontend dev server in another (or via `make start-fe`)
- keep browser dev tools open while iterating on UI work

If you want to validate the integrated result (frontend bundled into the backend JAR), use `./gradlew bootJar` instead of running each server separately.

---

## Frontend Workflow

### Main rule

Use **Vuetify components** for UI building.

The visual language should follow **Tabler-inspired styling**, but that styling is applied through the project's shared overrides rather than by bypassing Vuetify.

### Styling locations

Use the correct place for each kind of style:

- Local component-only styles → inside the Vue component, preferably scoped
- Global visual overrides for Vuetify components → `src/main/webapp/src/assets/tabler-overrides.scss`
- App-level global stylesheet entry → `src/main/webapp/src/assets/main.scss`
- Theme-level settings and shared tokens → `src/main/webapp/src/plugins/vuetify.ts`

Do not scatter global overrides across random component files.

### Style loading order

The style import order matters.

The current pattern is:

1. Vuetify base styles
2. MDI icon styles
3. App styles

That order must stay intact so that the shared overrides win predictably.

### Frontend folder usage

Use this structure consistently:

- `views/` for route-level pages
- `components/` for reusable UI pieces
- `composables/` for reusable reactive logic
- `services/` for API calls
- `stores/` for shared Pinia state
- `utils/` for pure helpers
- `models/` for TypeScript data shapes
- `layouts/` for app shells and layout wrappers
- `plugins/` for framework/plugin setup
- `assets/` for global styles and static assets

If a view gets too large, split it before adding more logic.

---

## Backend Workflow

### Build commands

```bash
# Build a production JAR (also builds and bundles the frontend automatically)
./gradlew bootJar

# Clean all build artifacts including frontend
./gradlew clean

# Check for dependency version updates
./gradlew dependencyUpdates
```

> **Note:** `./gradlew bootJar` depends on `buildFrontend`, which runs `npm install` then
> `npm run build` automatically via the `node-gradle` plugin.

### Frontend standalone build commands

```bash
npm run build       # production build → build/resources/main/static/
npm run type-check  # vue-tsc type check (no emit)
npm run lint        # ESLint with auto-fix
npm run format      # Prettier format src/
npm run preview     # preview production build locally
```

### Layering

Follow the existing backend flow:

```text
Controller -> Service -> Repository
```

Responsibilities should stay clear:

- Controllers handle HTTP concerns
- Services contain business logic and orchestration
- Repositories handle persistence only

Do not move business logic into controllers or repositories.

### Database changes

For schema changes:

- create a new Flyway migration
- use descriptive names
- never edit an already-applied migration

### AI-related work

When changing prompts, model inputs, output handling, or agent behavior:

- update the relevant prompt files
- update `AGENTS.md`
- update `DESIGN.md` if the change affects architecture or flow

Keep AI behavior explicit and documented.

---

## Testing During Development

Run tests early and often. See [`TESTING.md`](./TESTING.md) for the full strategy, conventions, and CI behaviour.

### Backend unit & integration tests

```bash
./gradlew test -Pprofile=test
```

Tests run against an H2 in-memory database using the `test` Spring profile.

### Frontend unit tests (Vitest)

```bash
npm run test:unit
```

### E2E tests (Cypress)

```bash
# Open Cypress against the running Vite dev server
npm run test:e2e:dev

# Headless Cypress run against a production preview build
npm run test:e2e
```

### AI-specific testing rules

- Never make real API calls in tests — mock `ChatClient`.
- Assert prompt variable injection, not LLM output content.
- Use `cy.intercept` in Cypress to stub all `/api/v1/ai/**` routes.

### When to run tests

Typical checkpoints:

- after changing backend business logic
- after changing auth/security behavior
- after changing prompt orchestration
- after changing shared frontend state
- before opening a PR

A practical local habit:

1. run the focused test set first
2. run the broader suite before pushing
3. manually verify the affected UI flow in the browser

---

## Daily Development Routine

A good default workflow is:

1. Pull the latest `develop`
2. Create a focused branch
3. Make one coherent change
4. Test the affected areas
5. Update docs if the change affects architecture, UI rules, AI behavior, or workflow
6. Commit with a clear message
7. Open a PR

Small, documented changes are easier to review and easier to trust.

---

## Documentation Workflow

The repository root markdown files are part of the development system, not an afterthought.

Update them whenever your change affects:

- architecture
- UI conventions
- AI behavior
- local setup
- security assumptions
- contribution workflow

The documentation set should also stay friendly to an Obsidian Vault approach:

- stable file names
- one main topic per file
- clear headings
- cross-links between related docs

---

## Definition of Done

A development task is not complete until:

- the code works locally
- the affected flow was tested
- the structure still follows the project conventions
- relevant docs were updated
- styling changes respect the Vuetify + Tabler approach
- no secrets or machine-specific noise were committed

Working code is necessary, but maintainable and understandable code is the real target.

---

## Troubleshooting Notes

When something feels off, check these first:

- wrong working directory for npm commands
- missing local environment variables
- backend running but frontend pointing to the wrong API base URL
- global CSS override added in the wrong place
- style import order changed accidentally
- new logic placed in a view instead of a composable, store, or service

Most local friction comes from one of those categories.

---

## Related Docs

- `AGENTS.md`
- `DESIGN.md`
- `CONTRIBUTING.md`
- `ARCHITECTURE.md`
- `API.md`
- `SECURITY.md`
- `DATA_MODEL.md`
- `TESTING.md`
- `PROMPTS.md`
- `AI_CONTEXT.md`
