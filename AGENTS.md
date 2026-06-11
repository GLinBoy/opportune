# AGENTS.md

> A living reference for both **human contributors** and **AI coding agents** (GitHub Copilot, Cursor, Claude, Gemini, etc.) working on the Opportune codebase. It covers build commands, code conventions, project structure, and the AI feature architecture — everything needed to contribute effectively without reading every source file first.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Quick Reference](#2-quick-reference)
3. [Environment Setup](#3-environment-setup)
4. [Project Structure](#4-project-structure)
5. [Build & Dev Commands](#5-build--dev-commands)
6. [Testing](#6-testing)
7. [Code Conventions](#7-code-conventions)
8. [AI Stack](#8-ai-stack)
9. [Agent Catalogue](#9-agent-catalogue)
10. [Prompt Architecture](#10-prompt-architecture)
11. [Data Flow](#11-data-flow)
12. [Security & Privacy Constraints](#12-security--privacy-constraints)
13. [Configuration Reference](#13-configuration-reference)
14. [Contributing to AI Features](#14-contributing-to-ai-features)
15. [Glossary](#15-glossary)

---

## 1. Project Overview

**Opportune** is a full-stack application that helps users craft tailored resumes and cover letters and track their job-search activity — all augmented by AI.

| Layer    | Technology                                                               |
| -------- | ------------------------------------------------------------------------ |
| Backend  | Kotlin + Spring Boot 4.0.x, Spring AI 2.0.0-M6 (OpenAI adapter)          |
| Frontend | Vue 3 + TypeScript + Vuetify 4, `md-editor-v3` (edit), `marked` (render) |
| Database | PostgreSQL (prod) / H2 (dev/test), migrations via Flyway                 |
| Build    | Gradle (Kotlin DSL) with `node-gradle` plugin for the frontend           |
| Auth     | JWT-based (Spring Security OAuth2 Resource Server) — stateless sessions  |

The AI layer is deliberately centralised in the backend so that the frontend never holds API keys or model parameters.

---

## 2. Quick Reference

Commands an AI agent should run first:

```bash
# Install frontend dependencies
npm install

# Run the full application in dev mode (backend + hot-reload frontend)
./gradlew bootRun -Pprofile=dev

# Or use Make (auto-detects tmux/screen/fallback)
make start       # start both backend and frontend
make stop        # stop both
make status      # check running services

# Run all backend tests
./gradlew test -Pprofile=test

# Run frontend unit tests
npm run test:unit

# Lint and format frontend code
npm run lint
npm run format

# Build a production JAR (includes frontend)
./gradlew bootJar
```

---

## 3. Environment Setup

> See [`DEVELOPMENT.md`](./DEVELOPMENT.md) for the complete local setup guide.

### Prerequisites

| Tool       | Version                                                           |
| ---------- | ----------------------------------------------------------------- |
| JDK        | 24 (managed by Gradle toolchain — downloaded automatically)       |
| Node.js    | 24.x (managed by `node-gradle` plugin — downloaded automatically) |
| PostgreSQL | 14+ (only required for `prod` profile; dev uses H2 in-memory)     |

### Environment files

The Gradle `bootRun` task loads variables from `.env.<profile>` files automatically. Copy the example and fill in required values:

```bash
cp .env.dev.example .env.dev   # adjust values as needed
```

**Required variables for `dev` profile:**

| Variable                                    | Required | Description                             |
| ------------------------------------------- | -------- | --------------------------------------- |
| `SPRING_AI_OPENAI_API_KEY`                  | Yes      | OpenAI secret key — never commit this   |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL`       | No       | Override model (default: `gpt-4o-mini`) |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_TEMPERATURE` | No       | Override temperature (default: `0.7`)   |

> **Note:** Profile env files (`.env.dev`, `.env.prod`) are git-ignored. Never hardcode
> secrets in `application.yml` or source files.

### Docker Compose (services only)

```bash
docker compose up -d   # starts PostgreSQL and any other backing services
```

The application itself is run via Gradle, not Docker, during development.

---

## 4. Project Structure

```
opportune/
├── .github/                         # GitHub workflows and templates
├── bin/                             # Build/run helper scripts
├── cypress/                         # E2E tests (Cypress)
│   └── e2e/
├── docs/                            # Additional documentation
├── gradle/                          # Gradle wrapper
├── src/
│   ├── main/
│   │   ├── kotlin/com/glinboy/opportune/
│   │   │   ├── aop/
│   │   │   │   ├── logging/         # Logging aspects
│   │   │   │   └── session/         # Session tracking aspects
│   │   │   ├── config/              # Spring configuration beans (Security, Jackson, OpenAPI, etc.)
│   │   │   ├── dto/                 # Data Transfer Objects (request/response shapes)
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── enums/               # Shared enumerations (Role, ApplicationStatus, etc.)
│   │   │   ├── event/               # Spring application events (e.g. ApplicationSubmittedEvent)
│   │   │   ├── mapper/              # MapStruct mappers (entity ↔ DTO)
│   │   │   ├── projection/          # JPA interface projections for lightweight queries
│   │   │   ├── repository/          # Spring Data JPA repositories
│   │   │   ├── security/
│   │   │   │   └── jwt/             # JWT utilities, SecurityUtils, auth converters
│   │   │   ├── service/
│   │   │   │   └── impl/            # Business logic implementations
│   │   │   ├── util/                # Shared utilities
│   │   │   └── web/
│   │   │       ├── controller/      # REST controllers
│   │   │       ├── filter/          # Servlet filters
│   │   │       └── rest/            # REST exception handlers / advice
│   │   ├── resources/
│   │   │   ├── config/              # application.yml, profile overrides
│   │   │   ├── db/migration/
│   │   │   │   └── h2/              # Flyway SQL migration scripts (H2 compat)
│   │   │   ├── prompts/             # Spring AI prompt templates (.st files)
│   │   │   └── templates/
│   │   │       └── mail/            # Thymeleaf email templates
│   │   └── webapp/                  # Vue 3 + TypeScript + Vuetify frontend
│   │       ├── public/
│   │       └── src/
│   │           ├── assets/
│   │           │   └── images/
│   │           ├── components/       # Reusable UI components
│   │           │   ├── __tests__/
│   │           │   ├── admin/dashboard/
│   │           │   ├── app-bar/
│   │           │   ├── application/
│   │           │   ├── company/
│   │           │   ├── dashboard/
│   │           │   ├── forms/
│   │           │   ├── icons/
│   │           │   ├── markdown/
│   │           │   ├── profile/
│   │           │   └── profile-menu/
│   │           ├── composables/      # Shared reactive logic (useXxx pattern)
│   │           ├── layouts/          # Page layout shells
│   │           ├── models/
│   │           │   └── enumerations/
│   │           ├── plugins/          # Vuetify plugin config
│   │           ├── router/           # Vue Router routes
│   │           ├── services/
│   │           │   └── admin/
│   │           ├── stores/           # Pinia stores
│   │           ├── utils/            # Pure helper functions
│   │           └── views/            # Page-level route components
│   │               ├── admin/
│   │               ├── application/
│   │               ├── auth/
│   │               ├── company/
│   │               └── profile/
│   └── test/
│       ├── kotlin/com/glinboy/opportune/
│       │   └── service/
│       │       └── impl/             # Mirror of main package layout
│       └── resources/
│           └── config/              # Test profile config (application-test.yml)
├── build.gradle.kts                  # Single Gradle build (backend + frontend tasks)
├── settings.gradle.kts               # Gradle project settings
├── compose.yaml                      # Docker Compose for backing services
├── Dockerfile                        # Multi-stage: builder (JDK 24) → runner (JRE alpine)
├── Makefile                          # Convenience targets: start, stop, restart, status
├── package.json                      # Frontend npm dependencies and scripts
├── package-lock.json
├── vite.config.ts                    # Vite build config with API proxy
├── vitest.config.ts                  # Vitest frontend test config
├── cypress.config.ts                 # Cypress E2E test config
├── tsconfig.json                     # TypeScript base config
├── tsconfig.app.json                 # TypeScript app config
├── tsconfig.node.json                # TypeScript Node config
├── tsconfig.vitest.json              # TypeScript Vitest config
├── env.d.ts                          # Vite env type declarations
├── eslint.config.ts                  # ESLint config
├── .prettierrc.json                  # Prettier config
├── .editorconfig                     # Editor config
├── .env.example                      # Environment variables template
├── .gitignore
├── .gitattributes
└── AGENTS.md                         # This file
```

**Key paths for AI work:**

- Prompt templates: `src/main/resources/prompts/`
- AI service implementation: `src/main/kotlin/.../service/impl/AiServiceImpl.kt`
- Security configuration: `src/main/kotlin/.../config/SecurityConfiguration.kt`
- REST controllers: `src/main/kotlin/.../web/`
- Flyway migrations: `src/main/resources/db/migration/`

---

## 5. Build & Dev Commands

### Backend

```bash
# Start backend with dev profile (H2 in-memory database)
./gradlew bootRun -Pprofile=dev

# Start backend with prod profile (PostgreSQL — requires compose.yaml services)
./gradlew bootRun -Pprofile=prod

# Build production JAR (also builds and bundles the frontend)
./gradlew bootJar

# Clean all build artefacts including frontend
./gradlew clean

# Check for dependency version updates
./gradlew dependencyUpdates
```

> **Gotcha:** `./gradlew bootJar` depends on `buildFrontend`, which runs `npm install` then
> `npm run build` automatically via the `node-gradle` plugin. Do not run them separately
> unless iterating on the frontend alone.

### Frontend (standalone)

```bash
npm install              # install dependencies (first time or after package.json changes)
npm run dev              # Vite dev server with HMR at http://localhost:5173
npm run build            # production build → build/resources/main/static/
npm run type-check       # vue-tsc type check (no emit)
npm run lint             # ESLint with auto-fix
npm run format           # Prettier format src/
```

### Make targets

```bash
make start      # start both backend (gradlew bootRun) and frontend (npm run dev)
make stop       # stop both
make start-be   # backend only
make start-fe   # frontend only
make attach-be  # tail backend logs (or attach tmux/screen session)
make attach-fe  # tail frontend logs
make status     # show running/stopped services
```

---

## 6. Testing

> See [`TESTING.md`](./TESTING.md) for the full testing strategy and conventions.

### Backend unit & integration tests

```bash
./gradlew test -Pprofile=test
```

- Tests run against H2 in-memory DB using the `test` Spring profile.
- Config: `src/test/resources/config/application-test.yml`
- Test files mirror the main package layout under `src/test/kotlin/`.

### Frontend unit tests (Vitest)

```bash
npm run test:unit
```

- Config: `vitest.config.ts`

### E2E tests (Cypress)

```bash
# Open Cypress against the running Vite dev server
npm run test:e2e:dev

# Headless Cypress run against a production preview build
npm run test:e2e
```

### AI-specific testing rules

- **Never make real API calls in tests.** Mock `ChatClient` / `StreamingChatClient`.
- Assert prompt _variable injection_, not LLM output content.
- Use `cy.intercept` in Cypress to stub all `/api/v1/ai/**` routes.
- Integration tests for AI features live under `src/test/kotlin/.../ai/`.

---

## 7. Code Conventions

### Kotlin (backend)

- **Kotlin 2.x** with `-Xjsr305=strict` — treat Spring's `@Nullable` / `@NonNull` annotations as Kotlin nullability.
- **No `var` in DTOs or entities** — prefer `val` and immutable data classes.
- **Data classes for DTOs** — use Kotlin `copy()` for updates instead of mutation.
- **`@Transactional` at service layer** — never on repository or controller methods.
- **JSON naming:** Jackson is configured with `PropertyNamingStrategies.SNAKE_CASE`. All API fields use `snake_case` (e.g. `profile_id`, `created_date`).
- **Logging:** Use `LoggerFactory.getLogger(this::class.java)` — never log prompt content at `INFO` or above (privacy constraint).
- **Security:** Always scope queries to the authenticated user via `SecurityUtils.getCurrentUserLoginID()`. Never return data across user boundaries.
- **JPA entities** annotated with `@Entity` / `@MappedSuperclass` must remain `open` (enforced by `allOpen` Gradle plugin).

### Vue 3 / TypeScript (frontend)

- **Composition API only** — no Options API.
- **`<script setup lang="ts">`** for all single-file components.
- **Vuetify 4** for UI components — do not mix in a secondary component library.
- **Pinia** for state management — one store per domain (e.g. `useApplicationStore`).
- **`axios`** for regular API calls; native `EventSource` for SSE streams.
- **No secrets in frontend code** — all AI calls go through backend endpoints.
- **User feedback:** Always notify the user of async outcomes (success, error, warning) using `useToastStore()` from `stores/toast.ts`. Never add a `<v-snackbar>` or local snackbar state inside a component — all notifications go through the global toast store. Every `catch` block in a service call must show an `error` toast; every successful destructive or long-running action must show a `success` toast. See §5 of `DESIGN.md` for the full notification design.

### Database migrations

- Flyway scripts live in `src/main/resources/db/migration/`.
- Naming: `V<version>__<description>.sql` (e.g. `V2__add_resume_score_columns.sql`).
- Never modify an already-applied migration — always create a new one.

---

## 8. AI Stack

| Layer                   | Technology                                                          | Notes                                        |
| ----------------------- | ------------------------------------------------------------------- | -------------------------------------------- |
| AI Framework            | [Spring AI](https://docs.spring.io/spring-ai/reference/) `2.0.0-M6` | Abstraction over LLM providers               |
| Default Provider        | OpenAI (via `spring-ai-starter-model-openai`)                       | Model configured per environment             |
| Prompt templating       | Spring AI `PromptTemplate` / `ChatClient`                           | `.st` files in `src/main/resources/prompts/` |
| Streaming               | Spring AI `Flux<String>` / Server-Sent Events                       | For long-form generation                     |
| Document pre-processing | `pdfbox` (PDF), `jsoup` (HTML)                                      | Raw text extracted before LLM call           |

> **Provider abstraction:** Swapping OpenAI for another provider (Anthropic, Ollama, etc.) requires only a configuration change — no code change — unless the feature uses provider-specific response formats.

---

## 9. Agent Catalogue

Each _agent_ here is a distinct AI interaction with its own purpose, system prompt, and input/output contract. They are request-scoped Spring service methods — not long-running autonomous processes.

**Implementation status legend:** ✅ Implemented · 🔲 Planned

### 9.1 Application Analysis Agent ✅

| Property         | Value                                                                                                                                       |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| **Purpose**      | Analyse a job application against the candidate's resume: score match, extract metadata, generate a cover letter and interview introduction |
| **Trigger**      | Automatically fires after a new application is saved (via `ApplicationSubmittedEvent`)                                                      |
| **Input**        | Sanitised job description text (jsoup), plain-text resume (pdfbox)                                                                          |
| **Output**       | Single JSON object (scores, cover letter, insights, company name, metadata)                                                                 |
| **Streaming**    | No — full structured JSON response required                                                                                                 |
| **Service**      | `AiServiceImpl.analysisApplication()`                                                                                                       |
| **Prompt files** | `src/main/resources/prompts/ai-analysis-system.st` (system), `ai-analysis-user.st` (user)                                                   |

> **JSON safety:** The system prompt includes explicit escaping rules because the output schema contains curly braces. The service strips any accidental ` ```json ` fences from the response before parsing.

### 9.2 Resume Builder Agent 🔲

| Property        | Value                                                                            |
| --------------- | -------------------------------------------------------------------------------- |
| **Purpose**     | Generate or improve a résumé section (summary, skills, experience bullets)       |
| **Trigger**     | User clicks "Generate" or "Improve" inside the resume editor                     |
| **Input**       | Job title, existing resume content (optional), target job description (optional) |
| **Output**      | Markdown-formatted resume section                                                |
| **Streaming**   | Yes — token-by-token via SSE                                                     |
| **Prompt file** | `src/main/resources/prompts/resume-builder.st` _(create this)_                   |

### 9.3 Cover Letter Agent 🔲

| Property        | Value                                                                   |
| --------------- | ----------------------------------------------------------------------- |
| **Purpose**     | Draft a personalised cover letter for a specific job application        |
| **Trigger**     | User clicks "Generate Cover Letter" on an application entry             |
| **Input**       | Company name, job title, job description excerpt, user's resume summary |
| **Output**      | Markdown-formatted cover letter                                         |
| **Streaming**   | Yes                                                                     |
| **Prompt file** | `src/main/resources/prompts/cover-letter.st` _(create this)_            |

### 9.4 Document Parser Agent ✅

| Property        | Value                                                                                              |
| --------------- | -------------------------------------------------------------------------------------------------- |
| **Purpose**     | Extract structured work experience, education, and skills from a PDF resume                        |
| **Trigger**     | `POST /api/profiles/resume/extract` — user-initiated; returns a preview that is never auto-saved   |
| **Input**       | Raw text extracted via `pdfbox` (PDF)                                                              |
| **Output**      | JSON object matching `ResumeExtractionResultDTO` with `work_experiences`, `education`, `skill_groups` |
| **Streaming**   | No — full structured JSON response required                                                        |
| **Prompt files**| `document-parser-system.st` (system), `document-parser-user.st` (user)                             |
| **Service**     | `DocumentParserServiceImpl.parseResumeData()`                                                      |
| **Endpoint**    | `POST /api/profiles/resume/extract` → `ProfileResumeDataResource`                                 |

### 9.5 Job Description Analyser Agent 🔲

| Property        | Value                                                                                  |
| --------------- | -------------------------------------------------------------------------------------- |
| **Purpose**     | Parse a raw job description and extract key requirements, keywords, and a summary      |
| **Trigger**     | User pastes or links a job description when creating an application                    |
| **Input**       | Raw job description text (plain text or HTML, sanitised by jsoup)                      |
| **Output**      | Structured list of must-have skills, nice-to-have skills, and a plain-language summary |
| **Streaming**   | No                                                                                     |
| **Prompt file** | `src/main/resources/prompts/job-description-analyser.st` _(create this)_               |

---

## 10. Prompt Architecture

> See [`PROMPTS.md`](./PROMPTS.md) for the complete prompt system documentation — template syntax, input/output contracts, update workflow, and testing procedures.

### Directory layout

```
src/main/resources/prompts/
├── ai-analysis-system.st       # Application Analysis Agent — system prompt
├── ai-analysis-user.st         # Application Analysis Agent — user prompt (variables: cleanJobDescription, extractedResumeText)
├── resume-builder.st           # Resume Builder Agent            [planned]
├── cover-letter.st             # Cover Letter Agent              [planned]
├── document-parser-system.st   # Document Parser Agent — system prompt (role + output schema)
├── document-parser-user.st     # Document Parser Agent — user prompt (variable: resumeText)
└── job-description-analyser.st # Job Description Analyser Agent  [planned]
```

Spring AI's `PromptTemplate` loads `.st` files from the classpath. Placeholder variables use `{variableName}` syntax.

> **Gotcha for JSON-heavy prompts:** If the system prompt embeds a JSON schema with curly braces, `PromptTemplate` may misparse them as variable placeholders. In that case, load the file as raw text and perform string substitution manually (see `AiServiceImpl` for the pattern).

### Prompt file structure

```
[SYSTEM]
Describe the AI's role, output format, and hard constraints.

[USER]
Template with {variable} placeholders filled at runtime.
```

### Prompt authoring principles

- **Be explicit about output format.** Markdown output? Say so. JSON output? Provide the full schema with field names and types.
- **Include a safety line.** Every prompt must contain: _"Do not invent information. If a field cannot be determined, leave it blank."_
- **Wrap user-supplied text in delimiters** to defend against prompt injection:
  ```
  ---BEGIN USER TEXT---
  {userContent}
  ---END USER TEXT---
  ```
- **Keep system prompts focused.** A few precise sentences outperform long paragraphs.
- **Never embed API keys or user PII in prompt files.** Use `{variable}` placeholders only.

---

## 11. Data Flow

```
User action (browser)
        │
        ▼
Vue 3 component
        │  HTTP POST /api/applications/submit-url
        ▼
Spring MVC Controller  ──► Jakarta Bean Validation
        │
        ▼
Application Service  (saves Application entity, fires ApplicationSubmittedEvent)
        │  (async — does not block the HTTP response)
        ▼
Event Listener → AI Service (Spring Bean)
        │  1. Fetch and sanitise job description (jsoup)
        │  2. Extract resume text (pdfbox)
        │  3. Load prompt templates from classpath
        │  4. Inject runtime variables into prompt templates
        │  5. Call ChatClient
        ▼
Spring AI  ──► OpenAI API (or configured provider)
        │
        ▼
AI results written back to Application entity
        │
        ▼
Vue 3 component polls GET /api/applications/{id}/details
        │  JSON: cover letter, scores, insights, interview introduction
        ▼
Vue 3 component (md-editor-v3 / marked renderer)
```

**Key points:**

- AI has no dedicated API endpoints. Processing is triggered asynchronously via `ApplicationSubmittedEvent` — the user's HTTP request is not blocked.
- All `/api/**` endpoints require authentication. Spring Security rejects unauthenticated requests before they reach the controller layer.
- User-uploaded documents are **never sent verbatim** to the model — only the plain text extracted by `pdfbox` / `jsoup`.
- The frontend polls `GET /api/applications/{id}/details` to retrieve AI-generated fields once processing completes.

---

## 12. Security & Privacy Constraints

> See [`SECURITY.md`](./SECURITY.md) for the full authentication and authorization model.

| Constraint                   | Implementation                                                                                                                              |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| No unauthenticated AI access | Spring Security blocks all `/api/**` without a valid JWT; AI processing is server-side only and never directly accessible from the frontend |
| No PII in logs               | AI service methods must not log prompt content at `INFO` or above                                                                           |
| User data isolation          | Every AI call is scoped to the authenticated user's data — no cross-user context injection                                                  |
| Prompt injection defence     | User-supplied text inserted into prompts must be wrapped in `---BEGIN USER TEXT--- / ---END USER TEXT---` delimiters                        |
| API key management           | OpenAI key loaded from `SPRING_AI_OPENAI_API_KEY` env var — never hardcoded                                                                 |
| Output sanitisation          | AI-generated HTML rendered through DOMPurify on the frontend before insertion into the DOM                                                  |

---

## 13. Configuration Reference

All AI configuration lives in `src/main/resources/config/application.yml` and profile-specific overrides (`application-dev.yml`, `application-prod.yml`).

```yaml
# application.yml (excerpt — do not paste real keys here)
spring:
  ai:
    openai:
      api-key: ${SPRING_AI_OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini # override per profile or env var
          temperature: 0.7
          max-tokens: 2048
```

**Environment variables:**

| Variable                                    | Required | Description                             |
| ------------------------------------------- | -------- | --------------------------------------- |
| `SPRING_AI_OPENAI_API_KEY`                  | Yes      | OpenAI secret key                       |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL`       | No       | Override model (default: `gpt-4o-mini`) |
| `SPRING_AI_OPENAI_CHAT_OPTIONS_TEMPERATURE` | No       | Override temperature (default: `0.7`)   |

Profile env files (`.env.dev`, `.env.prod`) are loaded automatically by the Gradle
`bootRun` / `bootJar` tasks via the `loadEnvFile()` function in `build.gradle.kts`.

---

## 14. Contributing to AI Features

Follow these steps when adding or extending an AI agent:

1. **Add a prompt file** in `src/main/resources/prompts/` following the structure in §10.
2. **Create or extend an AI service** in `service/impl/`, injecting `ChatClient` (or `StreamingChatClient` for SSE responses).
3. **Expose a controller endpoint** under `/api/...` following existing API path conventions — secured by default via Spring Security (no extra configuration needed for standard `/api/**` paths). For event-driven agents, the endpoint is the entry point that triggers the async flow (see §11) rather than a direct AI call.
4. **Document the agent** in §9 of this file and update its status from 🔲 to ✅.
5. **Write a unit test** that mocks `ChatClient` and asserts prompt variable injection — never assert on LLM output content.
6. **Run the full test suite** before submitting: `./gradlew test -Pprofile=test`.

---

## 15. Glossary

| Term                          | Meaning in this project                                                              |
| ----------------------------- | ------------------------------------------------------------------------------------ |
| **Agent**                     | A request-scoped Spring service method that calls the LLM for a specific task        |
| **Prompt template**           | A `.st` (StringTemplate) file in `src/main/resources/prompts/`                       |
| **ChatClient**                | Spring AI's synchronous LLM client                                                   |
| **StreamingChatClient**       | Spring AI's reactive LLM client returning `Flux<String>`                             |
| **SSE**                       | Server-Sent Events — transport for streaming AI responses to the browser             |
| **Provider**                  | The LLM backend (OpenAI by default; swappable via Spring AI config)                  |
| **Context injection**         | Inserting user data (resume text, job description) into a prompt at runtime          |
| **ApplicationSubmittedEvent** | Spring event fired after a new application is persisted; triggers async AI analysis  |
| **`loadEnvFile()`**           | Gradle helper that reads `.env.<profile>` and injects variables into the JVM process |
