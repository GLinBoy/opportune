# TODO

A short list of tasks to track feature work and refactors.

- [ ] Move all common properties from profile files into `src/main/resources/config/application.yml` — consolidate duplicated keys from `application-dev.yml`, `application-prod.yml`, etc.; keep only environment-specific overrides in profile files.

Notes:
- Do not move secrets (passwords, keys) to `application.yml`.
- After changes, validate application startup for each profile and run migrations/tests.

Created: 2025-09-04
Created by: GitHub Copilot

