# Changelog

All notable changes to Opportune are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versioning follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Added

- AI-readable markdown documentation files at repository root (`AGENTS.md`,
  `AI_CONTEXT.md`, `API.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`, `DATA_MODEL.md`,
  `DESIGN.md`, `DEVELOPMENT.md`, `PROMPTS.md`, `SECURITY.md`, `TESTING.md`,
  `CHANGELOG.md`) — see issue #124
- Spring AI integration (`spring-ai-starter-model-openai`, BOM `2.0.0-M6`) for
  resume and cover letter generation
- OAuth2 resource server with JWT authentication
- PDF processing via PDFBox
- HTML parsing via Jsoup
- GeoIP2 location detection and YAUAA user-agent analysis
- RSQL-based filtering on list endpoints
- Prometheus metrics endpoint via Micrometer
- Flyway database migrations with PostgreSQL support
- Frontend: Vue 3 + Vuetify 4 + Pinia + Vue Router 5
- Frontend: ECharts dashboards, FilePond file upload, md-editor-v3 markdown editor
- Frontend build orchestrated through Gradle `node-gradle` plugin
- Cypress 15 end-to-end test suite
- Vitest 4 frontend unit test suite
- Docker support via `Dockerfile` and `compose.yaml`
- Makefile for common development commands

### Changed

- Nothing yet

### Fixed

- Nothing yet

### Removed

- Nothing yet

---

## [0.0.1-SNAPSHOT] — In development

> Initial development snapshot. Not yet released.

---

<!-- Links -->

[Unreleased]: https://github.com/GLinBoy/opportune/compare/HEAD...HEAD
