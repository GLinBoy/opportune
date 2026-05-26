# Testing

Testing strategy and conventions for Opportune. The project uses three complementary layers:
**unit tests** on the backend (JUnit 5), **unit/component tests** on the frontend (Vitest),
and **end-to-end tests** (Cypress). All three must pass before merging into `develop` or `main`.

---

## Overview

| Layer         | Tool                       | Scope                                     | Location                            |
| ------------- | -------------------------- | ----------------------------------------- | ----------------------------------- |
| Backend unit  | JUnit 5 + Spring Boot Test | Services, repositories, controllers       | `src/test/kotlin/`                  |
| Frontend unit | Vitest + Vue Test Utils    | Vue components, Pinia stores, composables | `src/main/webapp/src/**/__tests__/` |
| End-to-end    | Cypress                    | Full user flows in a running app          | `cypress/e2e/`                      |

---

## Backend Tests

### Stack

- **JUnit 5** via `junit-platform-launcher`
- **Spring Boot Test** slices — prefer narrow slices over full `@SpringBootTest` where possible:
  - `@WebMvcTest` for controllers
  - `@DataJpaTest` for repositories
  - `@SpringBootTest` only for integration tests that require the full context
- **Spring Security Test** for auth scenarios (`@WithMockUser`, mock JWT)
- **H2 in-memory** database for test profile; Flyway migrations run automatically

### Running

```bash
# Run all backend tests
./gradlew test

# Run with a specific profile (defaults to "test")
./gradlew test -Pprofile=test
```

A `.env.test` file is loaded automatically if present. The test profile (`application-test.yml`) already provides a self-contained configuration — H2 in-memory database, hardcoded JWT secret, and disabled GeoIP — so a `.env.test` file is not required for the standard test suite.

### Conventions

- Test class names follow `<ClassName>Test` (e.g. `ResumeServiceTest`)
- One test class per production class
- Use `@DisplayName` on test methods for readable output
- Mock external integrations (OpenAI, mail, GeoIP) — never call live services in tests
- Assert on behaviour, not on implementation details

---

## Frontend Tests

### Stack

- **Vitest 4** — fast, Vite-native test runner
- **Vue Test Utils 2** — component mounting and interaction
- **jsdom** — DOM environment for component tests

### Running

```bash
# Run unit tests (watch mode)
npm run test:unit

# Run once (CI mode)
npx vitest run
```

### Conventions

- Test files live in a `__tests__/` subdirectory next to the source folder: `components/MyComponent.vue` → `components/__tests__/MyComponent.spec.ts`
- Test Pinia stores by instantiating them directly with `setActivePinia(createPinia())` from `pinia` — `@pinia/testing` is not installed
- Do not test Vuetify internals — test component behaviour and emitted events
- Keep tests free of hardcoded API URLs; mock axios with `vi.mock('axios')`

---

## End-to-End Tests

### Stack

- **Cypress 15** — browser-based full-stack tests
- Tests run against a built frontend served via `vite preview` (port 4173)

### Running

```bash
# Headless (CI)
npm run test:e2e

# Interactive (dev)
npm run test:e2e:dev
```

The `start-server-and-test` utility starts the preview server automatically before running Cypress.

### Conventions

- E2E tests live in `cypress/e2e/`
- Use `data-cy` attributes as selectors — never CSS classes or element tags
- Each spec file covers one user flow (e.g. `job-application-create.cy.ts`)
- Stub the auth layer using `cy.intercept` to mock login responses and pre-seed `localStorage` with a valid token — do not depend on a running backend for auth

---

## CI Behaviour

- Backend tests run on every push via Gradle (`./gradlew test -Pprofile=test`)
- `npm run build` runs `vue-tsc` type-checking and bundles the frontend — it does **not** run Vitest unit tests
- Frontend unit tests must be run explicitly: `npm run test:unit`
- Cypress tests are opt-in in CI — run them explicitly before release branches

---

## What to Test (and Why)

- **Services** — core business logic must have unit tests
- **Controllers** — test HTTP status codes, request validation, and auth rules
- **Repositories** — test custom JPQL/RSQL queries against H2
- **Vue components** — test user interactions and slot rendering, not visual output
- **Pinia stores** — test actions, getters, and state transitions
- **E2E** — cover the critical happy paths: login, create application, generate resume/cover letter

---

## Related

- `DEVELOPMENT.md` — local setup and running the app
- `ARCHITECTURE.md` — module boundaries relevant to test scope
- `AGENTS.md` — AI agents that must not be invoked during tests (mock them)

Created: 2026-05-25
