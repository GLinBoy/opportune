# API.md

This document describes the REST API of Opportune.

It covers API conventions, authentication model, endpoint groups, request/response structure, versioning, and error handling.

Read `ARCHITECTURE.md` for layering context. Read `SECURITY.md` for the full authentication and authorization model. Read `DATA_MODEL.md` for the entity shapes that underpin request and response payloads.

---

## Table of Contents

1. [Overview](#1-overview)
2. [Base URL and Versioning](#2-base-url-and-versioning)
3. [Authentication](#3-authentication)
4. [Request and Response Conventions](#4-request-and-response-conventions)
5. [Endpoint Groups](#5-endpoint-groups)
6. [Error Handling](#6-error-handling)
7. [Pagination](#7-pagination)
8. [AI Endpoints](#8-ai-endpoints)
9. [What Not To Do](#9-what-not-to-do)

---

## 1. Overview

The Opportune backend exposes a JSON REST API consumed by the Vue 3 frontend.

The API is stateless. Every request must carry an `Authorization: Bearer <token>` header. No session cookies are used.

The frontend communicates with the API exclusively through service files located at:

```text
src/main/webapp/src/services/
```

Do not scatter API calls across views or components. All HTTP communication belongs in the service layer.

---

## 2. Base URL and Versioning

### Base path

All API routes are prefixed with:

```text
/api
```

### Versioning

The current API is not explicitly versioned. When versioning becomes necessary, the recommended approach is path-based versioning:

```text
/api/v1/...
```

Document any versioning decisions here when they are introduced.

### Frontend proxy

During local development, the Vite dev server proxies `/api` requests to the Spring Boot backend. This is configured in `vite.config.ts` and should not require CORS handling in local development.

---

## 3. Authentication

All endpoints require authentication unless explicitly marked as public below.

### Token type

`Bearer` JWT token passed in the `Authorization` header:

```text
Authorization: Bearer <token>
```

### Token source

The backend issues its own JWTs — it is **not** delegating to an external OAuth2 provider. Tokens are obtained by calling `POST /api/auth/login` with email and password credentials. The response contains a short-lived access token and a longer-lived refresh token.

### User identity

The authenticated user's identity is derived from JWT claims on every request. There is no server-side session cookie. The backend resolves the current user from the JWT on each request via `SecurityUtils.getCurrentUserLoginID()`.

### Public endpoints

These endpoints do **not** require authentication:

| Method | Path                              | Description                                     |
| ------ | --------------------------------- | ----------------------------------------------- |
| `POST` | `/api/register`                   | Register a new account                          |
| `POST` | `/api/auth/login`                 | Log in, receive access + refresh tokens         |
| `POST` | `/api/auth/token/refresh`         | Exchange a refresh token for a new access token |
| `POST` | `/api/auth/password/reset/init`   | Initiate a password reset (sends email)         |
| `PUT`  | `/api/auth/password/reset/finish` | Complete a password reset with a code           |

`POST /api/auth/logout` requires a valid token (it terminates the current session).

---

## 4. Request and Response Conventions

### Content type

All request and response bodies use JSON:

```text
Content-Type: application/json
Accept: application/json
```

### Field naming

All JSON field names use `snake_case` (e.g. `profile_id`, `created_date`, `resume_overall_score`). This is enforced globally via Jackson's `PropertyNamingStrategies.SNAKE_CASE` in `JacksonConfig`.

### Timestamps

Return timestamps in ISO 8601 format:

```text
2026-05-25T14:30:00Z
```

### IDs

Return entity IDs as strings or numbers consistently. Do not mix types for the same entity across endpoints.

### Empty responses

Use `204 No Content` for successful operations that have no response body (e.g. delete).

### Nulls

Omit null fields from responses where they add no value, unless the frontend specifically needs to distinguish between missing and null.

---

## 5. Endpoint Groups

All endpoints below require authentication unless stated otherwise. Most list endpoints support RSQL filtering via a `query` parameter and Spring Data `Pageable` parameters (`page`, `size`, `sort`).

### Profile

| Method   | Path                                      | Description                                          |
| -------- | ----------------------------------------- | ---------------------------------------------------- |
| `GET`    | `/api/profiles/me`                        | Get the current user's profile                       |
| `GET`    | `/api/profiles`                           | List profiles (pageable, RSQL filterable)            |
| `GET`    | `/api/profiles/{id}`                      | Get a profile by ID                                  |
| `POST`   | `/api/profiles`                           | Create a profile                                     |
| `PUT`    | `/api/profiles`                           | Update the current user's profile                    |
| `DELETE` | `/api/profiles/{id}`                      | Delete a profile                                     |
| `PUT`    | `/api/profiles/password/change`           | Change the current user's password                   |
| `POST`   | `/api/profiles/email/verify/request`      | Request an email verification code                   |
| `PUT`    | `/api/profiles/email/confirm`             | Confirm email with a verification code (`?code=...`) |
| `GET`    | `/api/profiles/sessions`                  | List the current user's active sessions (pageable)   |
| `DELETE` | `/api/profiles/sessions/{refreshTokenId}` | Terminate a specific session                         |

---

### Profile Resume

| Method   | Path                                  | Description                                                           |
| -------- | ------------------------------------- | --------------------------------------------------------------------- |
| `GET`    | `/api/profiles/resumes`               | Get the current user's resume file metadata                           |
| `GET`    | `/api/profiles/resumes/{id}`          | Get a specific resume file metadata by ID                             |
| `POST`   | `/api/profiles/resumes`               | Upload a resume file (`multipart/form-data`, field: `profile_resume`) |
| `DELETE` | `/api/profiles/resumes`               | Delete the current user's resume file                                 |
| `DELETE` | `/api/profiles/resumes/{id}`          | Delete a resume file by ID                                            |
| `GET`    | `/api/profiles/resumes/download`      | Download the current user's resume file                               |
| `GET`    | `/api/profiles/resumes/{id}/download` | Download a resume file by ID                                          |

---

### Companies

| Method   | Path                                        | Description                                                   |
| -------- | ------------------------------------------- | ------------------------------------------------------------- |
| `GET`    | `/api/companies`                            | List the current user's companies (pageable, RSQL filterable) |
| `GET`    | `/api/companies/{id}`                       | Get a company by ID                                           |
| `POST`   | `/api/companies`                            | Create a company                                              |
| `PUT`    | `/api/companies`                            | Update a company                                              |
| `DELETE` | `/api/companies/{id}`                       | Delete a company                                              |
| `GET`    | `/api/companies/{company_id}/applications`  | List applications for a company (pageable)                    |
| `GET`    | `/api/companies/{company_id}/metadata`      | List metadata entries for a company (pageable)                |
| `GET`    | `/api/companies/{company_id}/metadata/{id}` | Get a metadata entry by ID                                    |
| `POST`   | `/api/companies/{company_id}/metadata`      | Add a metadata entry to a company                             |
| `PUT`    | `/api/companies/{company_id}/metadata/{id}` | Update a metadata entry                                       |
| `DELETE` | `/api/companies/{company_id}/metadata/{id}` | Delete a metadata entry                                       |

---

### Applications

| Method   | Path                             | Description                                                            |
| -------- | -------------------------------- | ---------------------------------------------------------------------- |
| `GET`    | `/api/applications/list`         | List applications for the current user (pageable, returns projections) |
| `GET`    | `/api/applications/{id}`         | Get a full application                                                 |
| `GET`    | `/api/applications/{id}/details` | Get extended application details (includes AI-generated fields)        |
| `POST`   | `/api/applications/submit-url`   | Submit a job URL — triggers AI analysis asynchronously                 |
| `PUT`    | `/api/applications`              | Update an application                                                  |
| `DELETE` | `/api/applications/{id}`         | Delete an application                                                  |

#### Application sub-resources

**Timelines**

| Method | Path                                                | Description                     |
| ------ | --------------------------------------------------- | ------------------------------- |
| `GET`  | `/api/applications/{application_id}/timelines`      | List timeline events (pageable) |
| `GET`  | `/api/applications/{application_id}/timelines/{id}` | Get a timeline event            |
| `POST` | `/api/applications/{application_id}/timelines`      | Add a timeline event            |
| `PUT`  | `/api/applications/{application_id}/timelines/{id}` | Update a timeline event         |

**Interview Notes**

| Method | Path                                                      | Description                     |
| ------ | --------------------------------------------------------- | ------------------------------- |
| `GET`  | `/api/applications/{application_id}/interview-notes`      | List interview notes (pageable) |
| `GET`  | `/api/applications/{application_id}/interview-notes/{id}` | Get an interview note           |
| `POST` | `/api/applications/{application_id}/interview-notes`      | Add an interview note           |
| `PUT`  | `/api/applications/{application_id}/interview-notes/{id}` | Update an interview note        |

**Interview Attachments**

| Method | Path                                                                                 | Description                                       |
| ------ | ------------------------------------------------------------------------------------ | ------------------------------------------------- |
| `GET`  | `/api/applications/{application_id}/interview-notes/{interview_note_id}/attachments` | List attachments for an interview note (pageable) |

**Application Resume**

| Method   | Path                                         | Description                               |
| -------- | -------------------------------------------- | ----------------------------------------- |
| `GET`    | `/api/applications/{application_id}/resumes` | Get the resume file for an application    |
| `DELETE` | `/api/applications/{application_id}/resumes` | Delete the resume file for an application |

**Application Attachments**

| Method   | Path                                                  | Description                                  |
| -------- | ----------------------------------------------------- | -------------------------------------------- |
| `GET`    | `/api/applications/{application_id}/attachments`      | List attachments (pageable)                  |
| `GET`    | `/api/applications/{application_id}/attachments/{id}` | Get an attachment                            |
| `DELETE` | `/api/applications/{application_id}/attachments/{id}` | Delete an attachment                         |
| `POST`   | `/api/applications/{application_id}/attachments`      | Upload an attachment (`multipart/form-data`) |

**Application Metadata**

| Method   | Path                                               | Description                      |
| -------- | -------------------------------------------------- | -------------------------------- |
| `GET`    | `/api/applications/{application_id}/metadata`      | List metadata entries (pageable) |
| `GET`    | `/api/applications/{application_id}/metadata/{id}` | Get a metadata entry             |
| `POST`   | `/api/applications/{application_id}/metadata`      | Add a metadata entry             |
| `PUT`    | `/api/applications/{application_id}/metadata/{id}` | Update a metadata entry          |
| `DELETE` | `/api/applications/{application_id}/metadata/{id}` | Delete a metadata entry          |

---

### Dashboard

| Method | Path                     | Description                                          |
| ------ | ------------------------ | ---------------------------------------------------- |
| `GET`  | `/api/dashboard/summary` | Get aggregated counts and stats for the current user |

---

### Search

| Method | Path                              | Description                                                   |
| ------ | --------------------------------- | ------------------------------------------------------------- |
| `GET`  | `/api/search?query=...`           | Full-text search across applications and companies (pageable) |
| `GET`  | `/api/search/companies?query=...` | Full-text search scoped to companies only (pageable)          |

---

## 6. Error Handling

### Standard error response shape

All error responses should return a consistent JSON body:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: jobDescription must not be blank",
  "path": "/api/ai/resume",
  "timestamp": "2026-05-25T14:30:00Z"
}
```

### HTTP status codes used

| Status                      | When to use                                            |
| --------------------------- | ------------------------------------------------------ |
| `200 OK`                    | Successful read or update                              |
| `201 Created`               | Successful resource creation                           |
| `204 No Content`            | Successful delete or action with no body               |
| `400 Bad Request`           | Validation failure or malformed request                |
| `401 Unauthorized`          | Missing or invalid token                               |
| `403 Forbidden`             | Authenticated but not allowed                          |
| `404 Not Found`             | Resource does not exist or does not belong to the user |
| `422 Unprocessable Entity`  | Valid structure but semantically invalid               |
| `500 Internal Server Error` | Unexpected server-side failure                         |

### Ownership enforcement

A user must never receive another user's data. Treat a resource owned by another user as `404 Not Found`, not `403 Forbidden`. Do not reveal that the resource exists.

### AI errors

AI provider errors should be caught in the service layer and returned as meaningful application errors. Do not propagate raw OpenAI error responses to the frontend.

---

## 7. Pagination

Paginated list endpoints return a **flat JSON array** in the response body. Pagination metadata is carried in HTTP response headers:

| Header          | Description                                                |
| --------------- | ---------------------------------------------------------- |
| `Link`          | RFC 5988 pagination links: `next`, `prev`, `first`, `last` |
| `X-Total-Count` | Total number of elements across all pages                  |

Both headers are exposed via `Access-Control-Expose-Headers` so the frontend can read them from `axios` responses.

Pagination query parameters (Spring Data `Pageable`):

| Parameter | Default | Description                                   |
| --------- | ------- | --------------------------------------------- |
| `page`    | `0`     | Zero-based page number                        |
| `size`    | `20`    | Page size                                     |
| `sort`    | none    | Field and direction, e.g. `created_date,desc` |

Filtering is supported on most list endpoints via an RSQL `query` parameter, e.g. `?query=status=="APPLIED"`.

---

## 8. AI Integration

There are no dedicated public AI endpoints. AI analysis is triggered **internally and asynchronously** — not via a separate API call from the frontend.

### How it works

1. The frontend calls `POST /api/applications/submit-url` with a job posting URL.
2. The backend saves the `Application` record and fires an `ApplicationSubmittedEvent`.
3. The event listener fetches the job description HTML, extracts the resume PDF text, and calls the AI provider.
4. The AI response (cover letter, resume insights, interview introduction, and all match scores) is written back to the `Application` entity.
5. The frontend polls or re-fetches `GET /api/applications/{id}/details` to see the populated AI fields once processing completes.

The `status` field on `Application` tracks AI processing state: `INITIATED` → `AI_PROCESSING` → `READY_TO_APPLY` (or an error state).

### AI-generated fields on Application

All AI-generated content is stored directly on the `Application` entity and returned via `GET /api/applications/{id}/details`:

- `cover_letter` — Markdown cover letter
- `resume_insights` — Markdown resume tailoring advice
- `interview_introduction` — Markdown self-introduction script
- `resume_overall_score`, `skill_match_score`, `experience_match_score`, `education_match_score`, `keyword_match_score` — 0–100 match scores
- Corresponding `*_rationale` fields for each score

See `AGENTS.md` for the full agent catalogue and `PROMPTS.md` for prompt file details.

---

## 9. What Not To Do

- Do not return raw entity objects from controllers — use DTOs.
- Do not expose internal database IDs unless the frontend needs them.
- Do not put business logic in controllers.
- Do not return AI provider errors directly to the frontend.
- Do not call the API from Vue views directly — always use the service layer in `services/`.
- Do not add unauthenticated endpoints without documenting them explicitly in this file.
- Do not silently swallow errors — return a meaningful status and message.

---

## Related Docs

- `ARCHITECTURE.md`
- `DATA_MODEL.md`
- `SECURITY.md`
- `AGENTS.md`
- `PROMPTS.md`
