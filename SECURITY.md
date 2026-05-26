# SECURITY.md

This document describes the security model of Opportune.

It covers authentication, authorization, token handling, data access rules, and security-related conventions for both backend and frontend.

Read `ARCHITECTURE.md` for the overall system structure. Read `API.md` for how security applies to specific endpoints.

---

## Table of Contents

1. [Overview](#1-overview)
2. [Authentication Model](#2-authentication-model)
3. [JWT Handling](#3-jwt-handling)
4. [Authorization Rules](#4-authorization-rules)
5. [Frontend Security](#5-frontend-security)
6. [Input Handling](#6-input-handling)
7. [AI Security Considerations](#7-ai-security-considerations)
8. [Secrets and Configuration](#8-secrets-and-configuration)
9. [What Not To Do](#9-what-not-to-do)

---

## 1. Overview

Opportune uses a stateless, token-based security model. The backend issues and validates its own JWTs — there is no external OAuth2 provider. Spring Security's OAuth2 Resource Server infrastructure is used to validate the self-signed tokens.

Core security properties:

- Stateless: no server-side HTTP sessions
- Token-based: every protected request must carry a valid JWT Bearer token
- Session tracking: every token pair is backed by a `Session` entity in the database, validated on each request
- User-scoped data: each user can only access their own data
- Explicit authorization: access control is enforced in the backend service layer
- Password management: passwords are hashed with BCrypt and managed entirely by the backend

---

## 2. Authentication Model

### Self-issued JWTs

The backend issues its own JWTs using HMAC-SHA512 (`HS512`) signed with a secret key loaded from `OPPORTUNE_JWT_SECRET`. Spring Security's OAuth2 Resource Server infrastructure (`NimbusJwtDecoder` / `NimbusJwtEncoder`) is used but configured programmatically in `SecurityJwtConfiguration` — there is no external OAuth2 provider.

The authentication flow:

1. User POSTs credentials to `POST /api/auth/login`
2. The backend validates the credentials, creates a `Session` entity, and returns an access token + refresh token pair
3. The client includes the access token as `Authorization: Bearer <token>` on subsequent requests
4. When the access token expires, the client calls `POST /api/auth/token/refresh` with the refresh token to get a new pair
5. Calling `POST /api/auth/logout` terminates the current session (marks it inactive in the database)

### Spring Security configuration

JWT signing and validation are configured in `SecurityJwtConfiguration`:

```yaml
# application.yml (excerpt)
opportune:
  security:
    authentication:
      jwt:
        base64-secret: ${OPPORTUNE_JWT_SECRET}
        token-validity-in-seconds: 86400 # 1 day (access token)
        token-validity-in-seconds-for-remember-me: 2592000 # 30 days
        refresh-token-validity-in-seconds: 2592000 # 30 days
```

There is no `spring.security.oauth2.resourceserver.jwt.issuer-uri` — the decoder is built directly from the secret key.

### User identity resolution

The custom `JwtAuthenticationConverter` handles each incoming request:

1. Extracts the `jti` claim (unique token ID) from the JWT
2. Looks up the corresponding `Session` entity in the database
3. Verifies the session status is `ACTIVE` — inactive or revoked sessions are rejected even if the JWT is not expired
4. Updates `last_active_at` on the session
5. Extracts the `roles` custom claim to build `GrantedAuthority` objects

The authenticated user's identity is the `sub` claim, which is the Profile UUID. `SecurityUtils.getCurrentUserLoginID()` parses it to `UUID`.

---

## 3. JWT Handling

### Token types

The backend issues two token types, distinguished by the `typ` claim:

| Type          | `typ` value | `aud` claim | Default validity |
| ------------- | ----------- | ----------- | ---------------- |
| Access token  | `Bearer`    | `account`   | 1 day            |
| Refresh token | `Refresh`   | `security`  | 30 days          |

### Claims on the access token

| Claim                 | Value         | Description                                                 |
| --------------------- | ------------- | ----------------------------------------------------------- |
| `sub`                 | Profile UUID  | Authenticated user identifier                               |
| `jti`                 | Random UUID   | Unique token ID — matched against `Session.access_token_id` |
| `iss`                 | App name      | Issuer (application name from config)                       |
| `aud`                 | `["account"]` | Audience                                                    |
| `iat` / `nbf` / `exp` | Instants      | Standard time claims                                        |
| `email`               | User email    |                                                             |
| `email_verified`      | Boolean       | Whether the user's email is confirmed                       |
| `forename`            | String        | User's first name                                           |
| `surname`             | String        | User's last name                                            |
| `name`                | String        | Full name (`forename + surname`)                            |
| `roles`               | String list   | Granted authorities, e.g. `["ROLE_USER"]`                   |
| `azp`                 | App name      | Authorised party                                            |
| `session_state`       | UUID          | Internal session correlation ID                             |
| `typ`                 | `Bearer`      | Token type discriminator                                    |

Refresh tokens carry only the common claims (`sub`, `jti`, `iss`, `aud`, `iat`, `nbf`, `exp`, `azp`, `session_state`) plus `typ=Refresh`.

### What the backend does NOT do

- The backend does not store full JWT token strings in the database (only the UUID `jti` references)
- The backend does not support opaque tokens or introspection endpoints

### Frontend token storage

Both the access token and refresh token are stored in `localStorage` by the `useAuthStore` Pinia store. Keys used: `accessToken`, `refreshToken`, `tokenType`, `expiresAt`, `refreshExpiresAt`.

Do not log tokens or include them in error messages.

---

## 4. Authorization Rules

### User-scoped data access

Every data resource in Opportune is owned by a user.

The core rule: **a user can only access their own data**.

Enforcement:

- All repository queries must filter by the authenticated user's ID
- The backend should treat another user's resource as `404 Not Found`, not `403 Forbidden` — do not reveal that the resource exists
- Never rely on the frontend to enforce data access rules

### Role-based access

Two roles are defined in the `Role` enum and enforced in `SecurityConfiguration`:

| Role         | Scope                              |
| ------------ | ---------------------------------- |
| `ROLE_USER`  | Required for all `/api/**` paths   |
| `ROLE_ADMIN` | Required for `/api/admin/**` paths |

`@EnableMethodSecurity(securedEnabled = true)` is active — individual methods can be annotated with `@Secured` for finer-grained control.

### Public endpoints

Endpoints that do not require authentication are explicitly permitted in `SecurityConfiguration`:

| Method | Path                                                                    | Notes                                           |
| ------ | ----------------------------------------------------------------------- | ----------------------------------------------- |
| `POST` | `/api/register`                                                         | Create a new account                            |
| `POST` | `/api/auth/login`                                                       | Obtain access + refresh tokens                  |
| `POST` | `/api/auth/token/refresh`                                               | Exchange a refresh token for a new access token |
| `POST` | `/api/auth/password/reset/init`                                         | Initiate password reset                         |
| `PUT`  | `/api/auth/password/reset/finish`                                       | Complete password reset                         |
| `PUT`  | `/api/profiles/email/confirm`                                           | Confirm email with verification code            |
| `GET`  | `/management/health`, `/management/info`, `/management/prometheus`      | Actuator endpoints                              |
| `GET`  | `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`                 | OpenAPI documentation                           |
| Any    | Static assets (`/`, `/index.html`, `/assets/**`, `*.css`, `*.js`, etc.) | SPA shell                                       |

All other `/api/**` paths require a valid `ROLE_USER` Bearer token.
Paths under `/api/admin/**` additionally require `ROLE_ADMIN`.

---

## 5. Frontend Security

### Route guarding

Protected routes must require authentication. The Vue Router configuration should guard all authenticated routes and redirect unauthenticated users to the login flow.

This should be implemented using a Vue Router navigation guard that checks for a valid token before allowing navigation to protected views.

### API communication

All API calls must go through the service layer in `src/main/webapp/src/services/`.

The Axios instance used by the services should attach the Bearer token via a request interceptor.

Example pattern:

```ts
axiosInstance.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

### Handling 401 responses

The Axios instance should include a response interceptor that catches `401 Unauthorized` responses and redirects to the login flow or triggers a token refresh.

### Markdown rendering safety

The application renders AI-generated and user-provided markdown.

Rules:

- Always sanitize HTML before inserting rendered markdown into the DOM
- Do not use `v-html` with unsanitized content
- The rendering layer should strip or escape any potentially unsafe HTML tags produced by the markdown renderer

---

## 6. Input Handling

### Backend validation

All request payloads must be validated before reaching the service layer.

Use Spring's validation annotations on request DTOs:

```kotlin
data class CreateJobDescriptionRequest(
    @field:NotBlank val rawText: String,
    @field:Size(max = 500) val title: String?,
    @field:URL val sourceUrl: String?
)
```

Return structured `400 Bad Request` responses for validation failures.

### Job description input

Job descriptions can be submitted as pasted text or scraped from a URL.

Security considerations:

- Strip HTML from scraped content before storing or passing to AI prompts (Jsoup is available for this)
- Validate that URLs are well-formed before attempting to scrape
- Do not follow unlimited redirects when scraping
- Enforce a maximum length on raw job description text

### AI prompt injection

User-supplied text is passed into AI prompts.

Rules:

- Never concatenate user input directly into prompt strings — use template variables
- Do not allow users to supply the system message or role instruction
- Validate input length before sending to the AI provider
- Log unexpected or malformed AI responses for review

---

## 7. AI Security Considerations

### Prompt injection

A malicious user could attempt to embed instructions in a job description or profile to manipulate AI outputs.

Mitigations:

- Use prompt templates with variable substitution — do not build prompts by string concatenation
- Keep the system/role instruction in the template, not derived from user input
- Validate and sanitize inputs before template substitution

### Output trust

AI outputs should never be trusted as safe content without review:

- Do not execute AI output as code
- Sanitize AI-generated markdown before rendering
- Log AI outputs that seem anomalous

### API key protection

The OpenAI API key must:

- never be committed to the repository
- never be exposed in frontend code or browser-accessible resources
- only be available to the backend at runtime via environment variables

---

## 8. Secrets and Configuration

### Secret management principle

No secrets should appear in committed code.

The following values must be injected at runtime via environment variables or a secrets manager:

| Secret                       | Purpose                                                         |
| ---------------------------- | --------------------------------------------------------------- |
| `OPPORTUNE_JWT_SECRET`       | Base64-encoded HMAC-SHA512 key for JWT signing and verification |
| `SPRING_AI_OPENAI_API_KEY`   | OpenAI (or compatible provider) API key for AI features         |
| `SPRING_DATASOURCE_URL`      | PostgreSQL JDBC connection string (prod)                        |
| `SPRING_DATASOURCE_USERNAME` | DB username (prod)                                              |
| `SPRING_DATASOURCE_PASSWORD` | DB password (prod)                                              |
| `OPPORTUNE_MAIL_HOST`        | SMTP server hostname                                            |
| `OPPORTUNE_MAIL_PORT`        | SMTP server port                                                |
| `OPPORTUNE_MAIL_USERNAME`    | SMTP credentials: username                                      |
| `OPPORTUNE_MAIL_PASSWORD`    | SMTP credentials: password                                      |

### Local development

Use `.env.<profile>` files (e.g. `.env.dev`) loaded automatically by the Gradle `loadEnvFile()` helper. These files are git-ignored.

Add all secret-containing files to `.gitignore` and verify they are excluded before committing.

### Production

Inject secrets via environment variables through your deployment platform. Do not hardcode secrets in `application.yml` or Docker files.

---

## 9. What Not To Do

- Do not bypass the `JwtAuthenticationConverter` session validation — always let the converter check session status before granting access
- Do not store full JWT token strings in the database — only persist the UUID `jti` references
- Do not expose another user's data — treat it as `404`, not `403`
- Do not put authorization logic in controllers — enforce it in services
- Do not rely on the frontend to enforce data access rules
- Do not commit `OPPORTUNE_JWT_SECRET`, AI API keys, database passwords, or mail credentials
- Do not insert AI-generated markdown into the DOM without sanitization
- Do not build AI prompts by concatenating user input strings
- Do not expose internal server error details to the frontend — return a clean error message
- Do not add public (`permitAll`) endpoints to `SecurityConfiguration` without also documenting them in `API.md` and Section 4 of this file

---

## Related Docs

- `ARCHITECTURE.md`
- `API.md`
- `DATA_MODEL.md`
- `AGENTS.md`
- `PROMPTS.md`
