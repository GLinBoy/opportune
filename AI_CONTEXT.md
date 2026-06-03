# AI_CONTEXT.md

This file is a compact shared context for Opportune.

It is intended to be useful for:

- human contributors who need a quick project briefing
- AI coding assistants that need repository-level context
- documentation systems such as an Obsidian Vault

This file is intentionally tool-agnostic. It does not target one specific LLM or editor integration.

---

## Project Identity

**Opportune** is a full-stack web application for helping users track their job-search activity and produce high-quality, tailored career documents — resumes, cover letters, and interview preparation materials — with AI assistance.

The system combines:

- a Kotlin / Spring Boot backend
- a Vue 3 frontend
- AI-assisted content generation and transformation
- shared repository-root documentation for contributors and AI tools

The project should be treated as one application with two major implementation layers: backend and frontend.

---

## Primary Goal

The main goal of the application is to help users produce high-quality, tailored, human-readable career documents with AI assistance.

AI should support the user, not replace judgment. Generated content should be:

- readable
- editable
- explainable
- structured
- safe to review before use

---

## Core Architectural Shape

At a high level:

```text
Frontend SPA
  -> Backend REST API
  -> Business / AI services
  -> Database and external providers
```

Backend flow:

```text
Controller -> Service -> Repository
```

Frontend flow:

```text
View -> Composable/Store -> Service -> API
```

Keep these boundaries intact when adding features.

---

## Repository Layout

The repository contains both backend and frontend code.

The verified frontend source root is:

```text
src/main/webapp/src/
```

That frontend currently contains these top-level folders:

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

Use those folders according to responsibility:

- `views/` for route-level pages
- `components/` for reusable UI
- `composables/` for shared reactive logic
- `stores/` for shared application state
- `services/` for API communication
- `models/` for TypeScript data shapes
- `utils/` for pure helper functions
- `layouts/` for layout shells
- `plugins/` for framework/plugin setup
- `assets/` for global styles and static assets

---

## Backend Expectations

The backend is the source of truth for:

- business rules
- security enforcement
- persistence
- AI orchestration
- document processing
- API contracts

Rules:

- controllers handle HTTP concerns
- services handle workflows and business logic
- repositories handle persistence only
- schema changes should be additive and migration-based
- AI calls should be explicit and documented

Do not push backend business rules into the frontend.

---

## Frontend Expectations

The frontend is responsible for:

- rendering the application
- collecting user input
- handling route-level UX
- managing local and shared state
- calling backend APIs
- displaying and editing markdown content

Rules:

- keep views thin
- move reusable logic into composables, stores, services, or utilities
- centralize API communication in `services/`
- prefer explicit models over loose anonymous objects
- keep styling consistent with the established design system

---

## UI System

The UI stack uses **Vuetify** as the component foundation and a **Tabler-inspired** visual language layered on top through shared overrides.

Important rule:

- use Vuetify components
- do not replace Vuetify with raw custom HTML equivalents just for styling
- apply visual consistency through the shared style system

The verified global override file is:

```text
src/main/webapp/src/assets/tabler-overrides.scss
```

That file currently contains global overrides for major Vuetify UI elements including cards, buttons, fields, navigation drawers, tables, dividers, elevations, app bars, list items, typography, tooltips, image behavior, and chart wrappers.

Use style locations intentionally:

- local component styles -> inside the component
- shared global visual overrides -> `tabler-overrides.scss`
- app-level style entry -> `main.scss`
- theme configuration -> `plugins/vuetify.ts`

---

## Style Loading Rule

The frontend entrypoint currently loads styles in this order:

1. Vuetify base styles
2. Material Design Icons
3. app styles from `./assets/main.scss`

This order matters because the project overrides need to load after Vuetify base styles in order to win predictably.

Do not reorder these imports casually.

---

## Charting Rule

Charts are set up globally through `vue-echarts`, with selected ECharts modules registered in the application entrypoint.

Implications:

- chart infrastructure belongs in shared application setup
- chart presentation belongs in views/components
- when adding a new chart type, follow the shared registration pattern instead of bootstrapping it ad hoc in a single component

---

## Markdown Rule

Markdown is a first-class content format in this project.

It is used because it is:

- readable by humans
- easy to edit
- easy to store
- easy to render
- well-suited to AI-generated structured text

Expect markdown-related features to follow this separation:

- editing layer
- rendering layer
- storage/persistence layer
- AI generation/transformation layer

Always optimize generated markdown for human readability first.

---

## AI Behavior Principles

AI in this project should be:

- task-specific
- explicit in its inputs
- explicit in its outputs
- constrained by documented prompts and flows
- reviewable by humans

AI should not behave like an uncontrolled black box.

When changing AI-related behavior:

- update the prompt or agent source
- update `AGENTS.md`
- update design or architecture docs when the change affects system flow

---

## Contributor Priorities

When making changes, optimize for:

1. clarity
2. consistency
3. maintainability
4. documentation quality
5. user-facing usefulness

Prefer an existing project pattern over inventing a new one.

If you introduce a new pattern, document it.

---

## What Not To Do

Avoid these common mistakes:

- putting business logic directly into controllers
- putting persistence logic into frontend code
- putting large amounts of logic directly into Vue views
- styling around Vuetify by replacing components with custom HTML
- scattering global CSS overrides across unrelated files
- creating undocumented AI behavior
- changing architecture without updating root documentation
- committing secrets or machine-specific local configuration

---

## What To Update When You Change Things

Use this quick map:

- AI behavior changed → update [`AGENTS.md`](./AGENTS.md)
- structural layering changed → update [`ARCHITECTURE.md`](./ARCHITECTURE.md)
- design or UI conventions changed → update [`DESIGN.md`](./DESIGN.md)
- local setup/workflow changed → update [`DEVELOPMENT.md`](./DEVELOPMENT.md)
- contribution process changed → update [`CONTRIBUTING.md`](./CONTRIBUTING.md)
- API contract changed → update [`API.md`](./API.md)
- security model changed → update [`SECURITY.md`](./SECURITY.md)
- data model or entities changed → update [`DATA_MODEL.md`](./DATA_MODEL.md)
- prompt templates changed → update [`PROMPTS.md`](./PROMPTS.md)
- test strategy changed → update [`TESTING.md`](./TESTING.md)

This file should stay short and high-signal. Put deeper detail in the specialized docs.

---

## Canonical Docs

Read these together:

- [`AGENTS.md`](./AGENTS.md)
- [`DESIGN.md`](./DESIGN.md)
- [`ARCHITECTURE.md`](./ARCHITECTURE.md)
- [`CONTRIBUTING.md`](./CONTRIBUTING.md)
- [`DEVELOPMENT.md`](./DEVELOPMENT.md)
- [`API.md`](./API.md)
- [`DATA_MODEL.md`](./DATA_MODEL.md)
- [`SECURITY.md`](./SECURITY.md)
- [`PROMPTS.md`](./PROMPTS.md)
- [`TESTING.md`](./TESTING.md)

---

## Intended Use

This file should be safe to read first before making code changes.

A human should be able to read it and quickly understand the project.

An AI tool should be able to read it and avoid obvious architectural and stylistic mistakes.

An Obsidian Vault should be able to link it as a central project context note.
