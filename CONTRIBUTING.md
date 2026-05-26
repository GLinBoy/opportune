# CONTRIBUTING.md

Thanks for contributing to **Opportune**.

This project combines a Kotlin/Spring Boot backend with a Vue 3 + Vuetify frontend, built together from one repository. Contributions should keep the codebase consistent, documented, and easy for both humans and AIs to understand.

---

## Table of Contents

1. [Scope](#scope)
2. [Project Structure](#project-structure)
3. [Environment Setup](#environment-setup)
4. [Build & Dev Commands](#build--dev-commands)
5. [Core Principles](#core-principles)
6. [Development Workflow](#development-workflow)
7. [Coding Conventions](#coding-conventions)
8. [Frontend Rules](#frontend-rules)
9. [Backend Rules](#backend-rules)
10. [Security Constraints](#security-constraints)
11. [Documentation Rules](#documentation-rules)
12. [Pull Requests](#pull-requests)
13. [Commit Messages](#commit-messages)

---

## Scope

Use this file as the main guide for contributing code, docs, prompts, styles, and architecture changes.

This repository prefers:

- small, focused changes
- explicit documentation for important decisions
- consistency over cleverness
- readable code over over-abstraction

---

## Project Structure

The repository contains both backend and frontend code in one place.

```text
opportune/
├── build.gradle.kts
├── package.json
├── AGENTS.md
├── DESIGN.md
├── CONTRIBUTING.md
├── src/main/
│   ├── kotlin/com/glinboy/opportune/
│   │   ├── aop/          # Cross-cutting concerns (logging, auditing aspects)
│   │   ├── config/       # Spring configuration beans (Security, Jackson, OpenAPI, etc.)
│   │   ├── dto/          # Data Transfer Objects (request/response shapes)
│   │   ├── entity/       # JPA entities
│   │   ├── enums/        # Shared enumerations (Role, ApplicationStatus, etc.)
│   │   ├── event/        # Spring application events (e.g. ApplicationSubmittedEvent)
│   │   ├── mapper/       # MapStruct mappers (entity ↔ DTO)
│   │   ├── projection/   # JPA interface projections for lightweight queries
│   │   ├── repository/   # Spring Data JPA repositories
│   │   ├── security/     # JWT utilities, SecurityUtils, auth converters
│   │   ├── service/      # Business logic (interfaces + impl/ sub-package)
│   │   ├── util/         # Shared utilities
│   │   └── web/          # REST controllers, filters, exception handlers
│   ├── resources/
│   │   ├── config/       # application.yml and profile overrides
│   │   ├── db/migration/ # Flyway SQL migration scripts
│   │   ├── prompts/      # Spring AI prompt templates (.st files)
│   │   └── templates/    # Thymeleaf email templates
│   └── webapp/
│       └── src/
│           ├── assets/       # SCSS entry points and tabler-overrides.scss
│           ├── components/   # Reusable UI components
│           ├── composables/  # Shared reactive logic (useXxx pattern)
│           ├── layouts/      # Page layout shells
│           ├── models/       # TypeScript interfaces — pure data shapes
│           ├── plugins/      # Vuetify plugin config (theme, defaults)
│           ├── router/       # Vue Router route definitions
│           ├── services/     # Axios API call wrappers — one file per domain
│           ├── stores/       # Pinia stores — one file per domain
│           ├── utils/        # Pure helper functions (no Vue reactivity)
│           └── views/        # Page-level components (route targets)
└── src/test/
    └── kotlin/               # Mirrors main package layout; test profile uses H2
```

When adding new files, place them in the most specific domain-oriented folder possible.

---

## Environment Setup

### Prerequisites

| Tool       | Version                                                           |
| ---------- | ----------------------------------------------------------------- |
| JDK        | 24 (managed by Gradle toolchain — downloaded automatically)       |
| Node.js    | 24.x (managed by `node-gradle` plugin — downloaded automatically) |
| PostgreSQL | 14+ (only required for `prod` profile; dev uses H2 in-memory)     |

### Environment file

The backend reads credentials and settings from a `.env.<profile>` file at startup.
Copy the provided example and fill in the required values:

```bash
cp .env.dev.example .env.dev   # never commit the filled-in file
```

**Required variables (`dev` profile):**

| Variable                                    | Required | Description                             |
| ------------------------------------------- | -------- | --------------------------------------- |
| `SPRING_AI_OPENAI_API_KEY`                  | Yes      | OpenAI secret key — never hardcode this |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL`       | No       | Override model (default: `gpt-4o-mini`) |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_TEMPERATURE` | No       | Override temperature (default: `0.7`)   |

Profile env files (`.env.dev`, `.env.prod`) are git-ignored. Gradle loads them
automatically via the `loadEnvFile()` helper in `build.gradle.kts`.

### Docker Compose (backing services only)

```bash
docker compose up -d   # starts PostgreSQL; only needed for the prod profile
```

The application itself is run via Gradle, not Docker, during development.

---

## Build & Dev Commands

### Backend

```bash
# Start the backend with the dev profile (H2 in-memory database)
./gradlew bootRun -Pprofile=dev

# Start the backend with the prod profile (requires PostgreSQL via compose.yaml)
./gradlew bootRun -Pprofile=prod

# Build a self-contained production JAR (also builds and bundles the frontend)
./gradlew bootJar

# Run all backend tests
./gradlew test -Pprofile=test

# Clean all build artefacts
./gradlew clean
```

> **Note:** `./gradlew bootJar` depends on `buildFrontend`, which runs `npm install`
> then `npm run build` automatically. Do not run them separately unless you are
> iterating on the frontend alone.

### Frontend (standalone)

```bash
npm install              # install dependencies (first time or after package.json changes)
npm run dev              # Vite dev server with HMR at http://localhost:5173
npm run build            # production build → build/resources/main/static/
npm run type-check       # vue-tsc type check (no emit)
npm run lint             # ESLint with auto-fix
npm run format           # Prettier format src/
```

### Make targets (convenience)

```bash
make start      # start both backend and frontend
make stop       # stop both
make start-be   # backend only
make start-fe   # frontend only
make attach-be  # tail backend logs
make attach-fe  # tail frontend logs
make status     # show running/stopped services
```

---

## Core Principles

### 1. Preserve the architecture

Follow the existing separation of responsibilities:

- Controllers handle HTTP concerns.
- Services contain business logic.
- Repositories contain persistence logic only.
- Views stay thin.
- Stores manage shared frontend state.
- Services on the frontend wrap API calls.
- Composables hold reusable reactive logic.

Do not move business logic into controllers, repositories, or large view components.

### 2. Prefer explicitness

Choose names and structures that make intent obvious:

- `ResumeService` is better than `CommonService`
- `user-profile.service.ts` is better than `helpers.ts`
- `JobMatchView.vue` is better than `MainPage.vue`

### 3. Document meaningful decisions

If your change affects architecture, AI behavior, prompts, security, styling strategy, or domain language, update the relevant markdown file in the repo root.

Code and docs should evolve together.

---

## Development Workflow

### Branching

Use feature branches for non-trivial work.

Suggested naming:

- `feature/...`
- `fix/...`
- `refactor/...`
- `docs/...`

Examples:

- `feature/resume-score-panel`
- `fix/oauth-redirect-loop`
- `docs/add-security-doc`

### Before starting

1. Read `AGENTS.md` if your change touches AI behavior.
2. Read `DESIGN.md` if your change affects architecture, UI patterns, or app structure.
3. Check whether an existing folder or service already covers your use case.
4. Prefer extending existing patterns over introducing a parallel one.

### Keep changes focused

A pull request should ideally do one thing well:

- one feature
- one bug fix
- one refactor
- one documentation topic

Avoid mixing unrelated UI, backend, and docs changes unless they are directly coupled.

---

## Coding Conventions

### General

- Prefer small, composable units.
- Avoid dead code and commented-out code.
- Do not add speculative abstractions "for future use".
- Keep naming domain-driven and consistent.
- Add comments only when the intent is not obvious from the code itself.

### File naming

Use the conventions already natural to the stack:

- Kotlin classes: `PascalCase`
- Vue single-file components: `PascalCase.vue`
- TypeScript utility/service/store files: consistent project-local naming, preferably descriptive and domain-based
- Markdown docs in repo root: uppercase file names like `AGENTS.md`, `DESIGN.md`, `CONTRIBUTING.md`

### Tests

Add or update tests when changing behavior, especially for:

- business logic
- auth/security behavior
- AI orchestration behavior
- parsing or transformation logic
- critical UI flows

**Backend unit & integration tests:**

```bash
./gradlew test -Pprofile=test
```

Tests run against H2 in-memory DB using the `test` Spring profile.
Config lives in `src/test/resources/config/application-test.yml`.

**Frontend unit tests (Vitest):**

```bash
npm run test:unit
```

**E2E tests (Cypress):**

```bash
# Open Cypress against the running Vite dev server
npm run test:e2e:dev

# Headless run against a production preview build
npm run test:e2e
```

**AI-specific testing rules:**

- **Never make real API calls in tests.** Mock `ChatClient` / `StreamingChatClient`.
- Assert prompt _variable injection_, not LLM output content.
- Use `cy.intercept` in Cypress to stub all `/api/v1/ai/**` routes.
- AI integration tests live under `src/test/kotlin/.../ai/`.

---

## Frontend Rules

### Use Vuetify components

This project uses **Vuetify components with a Tabler-inspired visual language**.

That means:

- use Vuetify for components
- do not replace Vuetify components with raw HTML just for styling
- apply the design language through theme/configuration and shared overrides

### Respect the Tabler styling approach

The Tabler-inspired look is implemented through shared stylesheet overrides.

Current global styling guidance:

- global component-level visual overrides belong in `src/main/webapp/src/assets/tabler-overrides.scss`
- app-level style entry belongs in `src/main/webapp/src/assets/main.scss`
- Vuetify theme configuration belongs in `src/main/webapp/src/plugins/vuetify.ts`
- local component-specific styles belong in scoped styles inside the component

Do not duplicate global styling rules in random component files.

### Keep views thin

In the frontend:

- `views/` are route-level screens
- `components/` are reusable UI pieces
- `stores/` hold shared state
- `services/` wrap API calls
- `composables/` hold reusable reactive logic
- `utils/` contain pure helpers

If a view becomes large, split it into components and composables.

### Charts

Charts are registered globally and should follow the existing chart setup. When introducing a new chart type, align with the current shared registration pattern instead of creating one-off chart bootstrapping inside a component.

Specifically: import and register the new chart type in `main.ts`, not inside individual components. Chart wrapper elements should carry the `.chart-wrapper` class to pick up the shared `tabler-overrides.scss` rule that enforces `width: 100%; aspect-ratio: 16/9; overflow: hidden`.

### Markdown features

If you work on markdown editing or rendering:

- editing should align with the existing markdown editor approach
- rendering should remain safe and predictable
- generated markdown should be readable by humans first

### Style loading order

The import order in `main.ts` is **intentional and must be preserved**:

```ts
import 'vuetify/styles' // 1. Vuetify base — lowest specificity
import '@mdi/font/css/materialdesignicons.css' // 2. Icon font
import './assets/main.scss' // 3. App styles (includes tabler-overrides.scss)
```

`tabler-overrides.scss` is imported from `main.scss` and wins over Vuetify by loading last.
Never reorder these imports.

### Theme compliance

The app ships two named Vuetify themes: `tablerLight` (default) and `tablerDark`.

Rules:

- **Never hardcode** `background`, `surface`, or text colours with hex values in components.
  Always use `var(--v-theme-*)` CSS custom properties or Vuetify's colour utility classes so
  the component works correctly in both themes.
- The only intentionally theme-fixed values are the structural neutral borders in
  `tabler-overrides.scss` (`rgba(101, 109, 119, …)`).
- Third-party components that have their own dark/light prop must be synced manually by
  reading `vuetifyTheme.global.current.value.dark`. Follow the pattern already used in
  `MdEditor.vue`.

### State management

Follow the Pinia conventions:

- **Stores** (`stores/`) manage server-fetched data, loading/error states, and domain actions.
- **Services** (`services/`) contain only Axios calls — no reactive state.
- **Composables** (`composables/`) contain reusable local reactive logic that does not need
  to be globally shared.
- Do not put Axios call logic directly inside Pinia actions — delegate to a service function
  and call it from the action.

### User feedback (toast notifications)

Every async operation that can succeed or fail must give the user visible feedback.
Silent failures are not acceptable.

Use the global `useToastStore()` from `stores/toast.ts`. Components call the store; they
do not own snackbar state themselves.

```ts
const toast = useToastStore()
try {
  await someApiCall()
  toast.success('Saved successfully')
} catch {
  toast.error('Something went wrong. Please try again.')
}
```

Rules:

- **Always** show a toast on API error — never swallow exceptions silently.
- **Always** confirm destructive or long-running actions with a success toast on completion.
- Use `toast.success()` / `toast.error()` / `toast.warning()` / `toast.info()`.
- **Never** add a `<v-snackbar>` or local snackbar reactive state inside a component.

---

## Backend Rules

### Service boundaries

Backend logic should generally follow this flow:

`Controller -> Service -> Repository`

Keep these responsibilities clear:

- Controller: HTTP mapping, validation boundary, response shape
- Service: orchestration, business rules, AI calls, parsing, workflows
- Repository: persistence only

### Database changes

When changing the schema:

- add a new Flyway migration
- never rewrite an already-applied migration
- keep migration names descriptive

### AI integration

If your change affects prompts, model inputs, AI outputs, or agent behavior:

1. **Add or update a prompt file** in `src/main/resources/prompts/` (`.st` extension,
   StringTemplate format). Follow the structure in [`AGENTS.md §10`](./AGENTS.md).
2. **Create or extend an AI service** in `service/impl/`, injecting `ChatClient`
   (or `StreamingChatClient` for SSE responses).
3. **Expose a controller endpoint** under `/api/v1/ai/{feature}` — secured by default
   via Spring Security (no extra configuration needed for standard API paths).
4. **Document the agent** in `AGENTS.md §9` and update its status from 🔲 to ✅.
5. **Write a unit test** that mocks `ChatClient` and asserts prompt variable injection —
   never assert on LLM output content.
6. **Run the full test suite** before submitting: `./gradlew test -Pprofile=test`.

**Prompt authoring rules:**

- Be explicit about the expected output format (Markdown, JSON, plain text).
- Include this safety line in every prompt:
  _"Do not invent information. If a field cannot be determined, leave it blank."_
- Wrap any user-supplied text in delimiters to defend against prompt injection:
  ```
  ---BEGIN USER TEXT---
  {userContent}
  ---END USER TEXT---
  ```
- Never embed API keys or user PII inside prompt files — use `{variable}` placeholders only.
- If the prompt includes a JSON schema with curly braces, load the file as raw text and
  perform string substitution manually (see `AiServiceImpl` for the established pattern).

Avoid hiding prompt logic across many unrelated files. Keep all prompt files in
`src/main/resources/prompts/` and all AI orchestration in the service layer.

### Security

Treat authentication and authorization changes as high-risk:

- keep JWT/OAuth-related changes minimal and reviewed
- document any new claim, role, scope, or access rule
- do not weaken defaults for convenience

---

## Security Constraints

Treat these rules as hard requirements, not guidelines:

| Constraint                   | Rule                                                                                                                                                         |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| No unauthenticated AI access | All `/api/v1/ai/**` endpoints are protected by Spring Security. Do not add bypass logic.                                                                     |
| No PII in logs               | AI service methods must **not** log prompt content at `INFO` or above.                                                                                       |
| User data isolation          | Every query and AI call must be scoped to the authenticated user via `SecurityUtils.getCurrentUserLoginID()`. Never return data across user boundaries.      |
| Prompt injection defence     | User-supplied text injected into prompts must be wrapped in `---BEGIN USER TEXT--- / ---END USER TEXT---` delimiters.                                        |
| API key management           | The OpenAI key is loaded from `SPRING_AI_OPENAI_API_KEY`. Never hardcode it in source files or `application.yml`.                                            |
| Output sanitisation          | AI-generated HTML rendered on the frontend must be passed through DOMPurify before insertion into the DOM.                                                   |
| JWT / OAuth changes          | Keep JWT/OAuth-related changes minimal and thoroughly reviewed. Do not weaken defaults for convenience. Document any new claim, role, scope, or access rule. |

When in doubt about a security decision, err on the side of restriction and document why.

---

## Documentation Rules

### Root documentation files

This repo uses root-level markdown files as shared operational memory for contributors and AI tools.

All root documentation files:

- `AGENTS.md`
- `DESIGN.md`
- `CONTRIBUTING.md`
- `DEVELOPMENT.md`
- `ARCHITECTURE.md`
- `API.md`
- `SECURITY.md`
- `DATA_MODEL.md`
- `PROMPTS.md`
- `TESTING.md`
- `AI_CONTEXT.md`

When a change introduces a new important concept, update docs in the same branch.

### Obsidian-friendly documentation

The documentation set should stay friendly to an **Obsidian Vault** workflow.

Preferred practices:

- clear headings
- stable file names
- links between docs using relative markdown links
- one main topic per file
- avoid burying architectural decisions inside issue threads only

Good examples:

- `[Design decisions](./DESIGN.md)`
- `[AI agent catalogue](./AGENTS.md)`

The goal is that the repo docs can also act as a lightweight knowledge graph for the application.

### Document the "why"

Document decisions, not just facts.

Good:

- "We use Vuetify components and apply Tabler styling through overrides to preserve accessibility and consistency."

Less useful:

- "There is a SCSS file."

---

## Pull Requests

### PR expectations

A good pull request should:

- have a clear title
- explain what changed
- explain why it changed
- mention any trade-offs
- note follow-up work if needed
- include docs updates when relevant

### PR checklist

Before opening a PR, confirm:

- [ ] The change is focused and understandable.
- [ ] New code follows existing project structure.
- [ ] Frontend work keeps Vuetify components and Tabler-style rules intact.
- [ ] Backend work respects controller/service/repository boundaries.
- [ ] Migrations are additive and correctly named.
- [ ] Related docs were updated.
- [ ] Tests were added or updated where appropriate.
- [ ] Removed code is actually unused.

### Review mindset

Reviews should optimize for:

- correctness
- maintainability
- consistency with existing architecture
- clarity for future contributors

---

## Commit Messages

Prefer conventional, descriptive commit messages.

Suggested format:

```text
type: short description
```

Examples:

- `feat: add resume tailoring panel`
- `fix: correct JWT claim mapping`
- `refactor: extract cover letter prompt service`
- `docs: add CONTRIBUTING.md`
- `style: refine tabler overrides for data tables`

Recommended types:

- `feat`
- `fix`
- `refactor`
- `docs`
- `test`
- `style`
- `chore`

---

## Final Notes

When in doubt:

1. follow the existing structure
2. avoid unnecessary abstraction
3. document important reasoning
4. prefer consistency over novelty

A contribution is complete only when the code, structure, and documentation still make sense together.
