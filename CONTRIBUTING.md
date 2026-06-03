# CONTRIBUTING.md

Thanks for contributing to **Opportune**.

This project combines a Kotlin/Spring Boot backend with a Vue 3 + Vuetify frontend, built together from one repository. Contributions should keep the codebase consistent, documented, and easy for both humans and AIs to understand.

---

## Table of Contents

1. [Scope](#scope)
2. [Core Principles](#core-principles)
3. [Development Workflow](#development-workflow)
4. [Documentation Rules](#documentation-rules)
5. [Pull Requests](#pull-requests)
6. [Commit Messages](#commit-messages)

---

## Scope

Use this file as the main guide for contributing code, docs, prompts, styles, and architecture changes.

This repository prefers:

- small, focused changes
- explicit documentation for important decisions
- consistency over cleverness
- readable code over over-abstraction

### Quick Reference — where to find the rules

| Topic                    | Primary document                 |
| ------------------------ | -------------------------------- |
| Project structure        | [`AGENTS.md §4`](./AGENTS.md)    |
| Local setup & commands   | [`DEVELOPMENT.md`](./DEVELOPMENT.md) |
| Kotlin conventions       | [`AGENTS.md §7`](./AGENTS.md)    |
| Frontend conventions     | [`DESIGN.md §4–5`](./DESIGN.md)  |
| UI design language       | [`DESIGN.md §5`](./DESIGN.md)    |
| AI agents & prompts      | [`AGENTS.md §9–10`](./AGENTS.md) |
| API contract             | [`API.md`](./API.md)             |
| Data model               | [`DATA_MODEL.md`](./DATA_MODEL.md) |
| Security model           | [`SECURITY.md`](./SECURITY.md)   |
| Testing strategy         | [`TESTING.md`](./TESTING.md)     |
| Prompt template details  | [`PROMPTS.md`](./PROMPTS.md)     |
| Architecture decisions   | [`ARCHITECTURE.md`](./ARCHITECTURE.md) |

---

## Core Principles

### 1. Preserve the architecture

Follow the existing separation of responsibilities — see [`ARCHITECTURE.md`](./ARCHITECTURE.md) for the full layering rules.

At a high level:

- **Backend:** Controllers handle HTTP concerns, Services contain business logic, Repositories contain persistence logic only.
- **Frontend:** Views stay thin, Stores manage shared state, Services wrap API calls, Composables hold reusable reactive logic.

Do not move business logic into controllers, repositories, or large view components.

### 2. Prefer explicitness

Choose names and structures that make intent obvious:

- `ResumeService` is better than `CommonService`
- `user-profile.service.ts` is better than `helpers.ts`
- `JobMatchView.vue` is better than `MainPage.vue`

### 3. Document meaningful decisions

If your change affects architecture, AI behavior, prompts, security, styling strategy, or domain language, update the relevant markdown file in the repo root. See [Documentation Rules](#documentation-rules) below for which file to update.

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

1. Read [`AGENTS.md`](./AGENTS.md) if your change touches AI behavior.
2. Read [`DESIGN.md`](./DESIGN.md) if your change affects architecture, UI patterns, or app structure.
3. Check whether an existing folder or service already covers your use case.
4. Prefer extending existing patterns over introducing a parallel one.

### Keep changes focused

A pull request should ideally do one thing well:

- one feature
- one bug fix
- one refactor
- one documentation topic

Avoid mixing unrelated UI, backend, and docs changes unless they are directly coupled.

### Daily routine

1. Pull the latest `develop`
2. Create a focused branch
3. Make one coherent change
4. Test the affected areas — run backend tests (`./gradlew test -Pprofile=test`) and frontend unit tests (`npm run test:unit`). See [`TESTING.md`](./TESTING.md) for full strategy.
5. Update docs if the change affects architecture, UI rules, AI behavior, or workflow
6. Commit with a clear message
7. Open a PR

---

## Documentation Rules

### Root documentation files

This repo uses root-level markdown files as shared operational memory for contributors and AI tools. When a change introduces a new important concept, update docs in the same branch.

### What to update when

| Change affects…                  | Update…                            |
| -------------------------------- | ---------------------------------- |
| AI behavior, prompts, agents     | [`AGENTS.md`](./AGENTS.md)         |
| Structural layering              | [`ARCHITECTURE.md`](./ARCHITECTURE.md) |
| UI conventions, design language  | [`DESIGN.md`](./DESIGN.md)         |
| Local setup, dev commands        | [`DEVELOPMENT.md`](./DEVELOPMENT.md) |
| API contract, endpoints          | [`API.md`](./API.md)               |
| Security model                   | [`SECURITY.md`](./SECURITY.md)     |
| Data model, entities             | [`DATA_MODEL.md`](./DATA_MODEL.md) |
| Prompt templates                 | [`PROMPTS.md`](./PROMPTS.md)       |
| Test strategy                    | [`TESTING.md`](./TESTING.md)       |
| Contribution process             | This file                         |

### Obsidian-friendly documentation

The documentation set should stay friendly to an **Obsidian Vault** workflow.

Preferred practices:

- clear headings
- stable file names
- links between docs using relative markdown links
- one main topic per file
- avoid burying architectural decisions inside issue threads only

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
