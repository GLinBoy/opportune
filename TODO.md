# TODO

A short list of tasks to track feature work and refactors.

- [ ] Move all common properties from profile files into `src/main/resources/config/application.yml` — consolidate
      duplicated keys from `application-dev.yml`, `application-prod.yml`, etc.; keep only environment-specific overrides in
      profile files.

- [ ] Add Terms of Service (ToS) document to the project and expose it in Swagger UI (OpenAPI) —
      provide a static ToS file or an endpoint, and configure the OpenAPI "termsOfService" URL or Swagger UI link so the
      ToS is discoverable from the API docs.

- [ ] When user selects an item (company/application) from the page list, put it in store state (or sort of pass it to the detail page) and then web app doesn't need to fetch it again (or it can fetch it and update it afterward) — optimize data flow by caching selected items in the frontend state management to reduce redundant API calls.

Notes:

- Do not move secrets (passwords, keys) to `application.yml`.
- After changes, validate application startup for each profile and run migrations/tests.

Implementation notes:

- Add a markdown or HTML ToS file under src/main/resources/static/terms-of-service.html (or similar) or create an
  endpoint that returns the ToS content.
- Update OpenAPI/Swagger configuration (e.g. springdoc-openapi or springfox) to set the termsOfService URL to the
  public path of the ToS, or add a custom link in the Swagger UI configuration.
- Ensure the ToS file is included in packaged resources and accessible at runtime (e.g. /static/terms-of-service.html).
- Add tests or a smoke check that the Swagger UI shows the ToS link.

Created: 2025-09-04
Created by: GitHub Copilot
