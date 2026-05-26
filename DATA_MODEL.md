# DATA_MODEL.md

This document describes the data model of Opportune.

It covers the core domain entities, their relationships, key design decisions, and conventions for evolving the schema.

Read `ARCHITECTURE.md` for backend layering. Read `AGENTS.md` for how AI agents interact with these data structures.

---

## Table of Contents

1. [Purpose](#1-purpose)
2. [Domain Overview](#2-domain-overview)
3. [Core Entities](#3-core-entities)
4. [Entity Relationships](#4-entity-relationships)
5. [Flyway Migration Conventions](#5-flyway-migration-conventions)
6. [JPA Conventions](#6-jpa-conventions)
7. [DTO Conventions](#7-dto-conventions)
8. [What Not To Do](#8-what-not-to-do)

---

## 1. Purpose

The data model is the backbone of Opportune.

Every AI-assisted feature — resume tailoring, cover letter generation, job description parsing — is grounded in user-owned data persisted in the database.

This file exists so that:

- contributors understand the domain vocabulary
- new code uses consistent naming
- schema decisions are explicit and visible
- AI tools can understand entities without reading all source code

---

## 2. Domain Overview

Opportune helps users track their job search activity and generate AI-assisted career documents.

The domain vocabulary:

| Term                    | Meaning                                                                                                                                                                               |
| ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Profile**             | The user/account entity — authentication data, personal info, roles, and the link to all owned data                                                                                   |
| **Company**             | A company a user tracks — stores firmographic data, status, and links to applications                                                                                                 |
| **Application**         | The core entity — a job application containing the job posting data, AI-generated content (cover letter, resume insights, interview introduction), match scores, and lifecycle status |
| **ApplicationTimeline** | An ordered log of status-change events for a given application                                                                                                                        |
| **InterviewNote**       | Notes captured during or after an interview, linked to an application                                                                                                                 |
| **ProfileResume**       | The user's uploaded resume file (1:1 with Profile)                                                                                                                                    |
| **ApplicationResume**   | A resume file uploaded specifically for one application (1:1 with Application)                                                                                                        |
| **Session**             | A JWT session record tracking access/refresh tokens, device info, and geo-location                                                                                                    |
| **VerificationCode**    | A short-lived code used for email verification flows                                                                                                                                  |

---

## 3. Core Entities

### Base classes

All persistent entities extend one of two abstract base classes:

**`BaseEntity`** — provides the `@Id` field using `GenerationType.UUID`.

**`AuditableEntity`** (extends `BaseEntity`) — adds `createdDate` and `lastModifiedDate` as `Instant` fields. Most domain entities extend `AuditableEntity`.

Two abstract entity hierarchies use `InheritanceType.TABLE_PER_CLASS`:

- **`Attachment`** — base for file-upload entities (`name`, `path`, `contentType`, `contentLength`). Concrete subtypes: `ProfileResume`, `ApplicationResume`, `ApplicationAttachment`, `InterviewAttachment`.
- **`MetaData`** — base for key-value metadata entities (`metaName`, `metaValue`). Concrete subtypes: `CompanyMetaData`, `ApplicationMetaData`.

---

### Profile

The user/account entity. A `Profile` record is created on registration and serves as the ownership root for all user data.

Key fields:

| Field               | Type                 | Notes                                         |
| ------------------- | -------------------- | --------------------------------------------- |
| `id`                | UUID                 | PK, generated                                 |
| `email`             | String               | Unique, not null                              |
| `forename`          | String               | Given name                                    |
| `surname`           | String               | Family name                                   |
| `password`          | String               | Hashed password, not null                     |
| `jobTitle`          | String               | Current or target job title                   |
| `location`          | String               | User's location                               |
| `avatar`            | String               | Avatar file path                              |
| `emailVerification` | Boolean              | Whether email is verified                     |
| `lastLogin`         | Instant              | Last login timestamp                          |
| `status`            | `AccountStatus`      | `ACTIVE`, `SUSPENDED`, `PENDING_VERIFICATION` |
| `subscription`      | String               | Subscription tier                             |
| `roles`             | Set\<`Role`\>        | Stored in `profile_role` join table           |
| `resume`            | `ProfileResume`      | 1:1 uploaded resume file                      |
| `applications`      | Set\<`Application`\> | All applications owned by this profile        |
| `companies`         | Set\<`Company`\>     | All companies tracked by this profile         |

---

### Company

A company the user tracks — either as a target to apply to or as context for existing applications.

Key fields:

| Field          | Type                     | Notes                                     |
| -------------- | ------------------------ | ----------------------------------------- |
| `id`           | UUID                     | PK, generated                             |
| `name`         | String                   | Company name                              |
| `industry`     | String                   | Industry sector                           |
| `website`      | String                   | Website URL                               |
| `companySize`  | String                   | Size band (e.g. "50-200")                 |
| `location`     | String                   | Headquarters location                     |
| `foundedYear`  | String                   | Year founded                              |
| `description`  | Text                     | Company description                       |
| `note`         | Text                     | User's private notes                      |
| `logo`         | String                   | Logo file path                            |
| `status`       | `CompanyStatus`          | `INTERESTED`, `NOT_INTERESTED`, `BLOCKED` |
| `profile`      | `Profile`                | Owning profile (FK)                       |
| `applications` | Set\<`Application`\>     | Applications at this company              |
| `metaData`     | Set\<`CompanyMetaData`\> | Key-value metadata entries                |

---

### Application

The central entity. Stores the job posting data (captured from a URL or pasted), all AI-generated content, and match scores produced by the Application Analysis Agent.

Key fields:

| Field                      | Type                           | Notes                                                                                                                          |
| -------------------------- | ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------ |
| `id`                       | UUID                           | PK, generated                                                                                                                  |
| `url`                      | String                         | Source URL of the job posting                                                                                                  |
| `title`                    | String                         | Job title                                                                                                                      |
| `location`                 | String                         | Job location                                                                                                                   |
| `appliedAt`                | Instant                        | When the user submitted the application                                                                                        |
| `salary`                   | String                         | Salary information                                                                                                             |
| `note`                     | Text                           | User's private notes                                                                                                           |
| `rawContent`               | Text                           | Raw job posting HTML/text                                                                                                      |
| `description`              | Text                           | Cleaned job description text                                                                                                   |
| `status`                   | `ApplicationStatus`            | `INITIATED`, `AI_PROCESSING`, `READY_TO_APPLY`, `APPLIED`, `IN_PROGRESS`, `REJECTED`, `OFFER_RECEIVED`, `ACCEPTED`, `DECLINED` |
| `coverLetter`              | Text                           | AI-generated cover letter (Markdown)                                                                                           |
| `resumeInsights`           | Text                           | AI-generated resume insights                                                                                                   |
| `interviewIntroduction`    | Text                           | AI-generated interview introduction                                                                                            |
| `resumeOverallScore`       | Int                            | 0–100 overall match score                                                                                                      |
| `skillMatchScore`          | Int                            | 0–100 skill match score                                                                                                        |
| `skillMatchRationale`      | Text                           | AI rationale for skill score                                                                                                   |
| `experienceMatchScore`     | Int                            | 0–100 experience match score                                                                                                   |
| `experienceMatchRationale` | Text                           | AI rationale for experience score                                                                                              |
| `educationMatchScore`      | Int                            | 0–100 education match score                                                                                                    |
| `educationMatchRationale`  | Text                           | AI rationale for education score                                                                                               |
| `keywordMatchScore`        | Int                            | 0–100 keyword match score                                                                                                      |
| `keywordMatchRationale`    | Text                           | AI rationale for keyword score                                                                                                 |
| `company`                  | `Company`                      | Associated company (FK, nullable)                                                                                              |
| `profile`                  | `Profile`                      | Owning profile (FK)                                                                                                            |
| `resume`                   | `ApplicationResume`            | 1:1 resume file for this application                                                                                           |
| `timeline`                 | Set\<`ApplicationTimeline`\>   | Status-change history                                                                                                          |
| `interviewNotes`           | Set\<`InterviewNote`\>         | Interview notes                                                                                                                |
| `metadata`                 | Set\<`ApplicationMetaData`\>   | Key-value metadata entries                                                                                                     |
| `attachments`              | Set\<`ApplicationAttachment`\> | Additional file attachments                                                                                                    |

---

### ApplicationTimeline

A log entry recording a status change in the lifecycle of an application.

Key fields:

| Field         | Type                | Notes                         |
| ------------- | ------------------- | ----------------------------- |
| `id`          | UUID                | PK, generated                 |
| `title`       | String              | Short label for the event     |
| `description` | Text                | Details of the event          |
| `status`      | `ApplicationStatus` | Status at this timeline point |
| `occurredAt`  | Instant             | When the event occurred       |
| `application` | `Application`       | Owning application (FK)       |

---

### InterviewNote

Notes captured during or after an interview, linked to an application.

Key fields:

| Field         | Type                          | Notes                   |
| ------------- | ----------------------------- | ----------------------- |
| `id`          | UUID                          | PK, generated           |
| `date`        | Instant                       | Interview date/time     |
| `notes`       | Text                          | Free-form notes         |
| `application` | `Application`                 | Owning application (FK) |
| `attachments` | List\<`InterviewAttachment`\> | Attached files          |

---

### Attachment subtypes

All extend the abstract `Attachment` base class (`name`, `path`, `contentType`, `contentLength`):

| Entity                  | Table                    | Unique constraint        |
| ----------------------- | ------------------------ | ------------------------ |
| `ProfileResume`         | `profile_resume`         | 1:1 with `Profile`       |
| `ApplicationResume`     | `application_resume`     | 1:1 with `Application`   |
| `ApplicationAttachment` | `application_attachment` | N:1 with `Application`   |
| `InterviewAttachment`   | `interview_attachment`   | N:1 with `InterviewNote` |

---

### MetaData subtypes

All extend the abstract `MetaData` base class (`metaName`, `metaValue`):

| Entity                | Table                   | Parent FK                        |
| --------------------- | ----------------------- | -------------------------------- |
| `CompanyMetaData`     | `company_meta_data`     | `company_id` → `Company`         |
| `ApplicationMetaData` | `application_meta_data` | `application_id` → `Application` |

---

### Session

Tracks active JWT sessions for security and multi-device management.

Key fields:

| Field                    | Type               | Notes                          |
| ------------------------ | ------------------ | ------------------------------ |
| `refreshTokenId`         | UUID               | PK (the refresh token's JTI)   |
| `accessTokenId`          | UUID               | Unique index for O(1) lookup   |
| `accessTokenExpiration`  | Instant            | When the access token expires  |
| `refreshTokenExpiration` | Instant            | When the refresh token expires |
| `status`                 | `SessionStatus`    | `ACTIVE`, `EXPIRED`, `REVOKED` |
| `clientAgent`            | String             | User-Agent header              |
| `clientIp`               | String             | Client IP address              |
| `clientGeo`              | String             | Geo-resolved location          |
| `deviceId`               | String             | Fingerprint-derived device ID  |
| `deviceType`             | String             | Desktop / Mobile / Tablet      |
| `os` / `browser`         | String             | Parsed from user-agent         |
| `revocationReason`       | `RevocationReason` | Why the session was revoked    |
| `profile`                | `Profile`          | Owning profile (FK)            |

---

### VerificationCode

A short-lived code used in email verification flows.

Key fields:

| Field     | Type                   | Notes               |
| --------- | ---------------------- | ------------------- |
| `id`      | UUID                   | PK, generated       |
| `type`    | `VerificationCodeType` | Purpose of the code |
| `profile` | `Profile`              | Owning profile (FK) |

---

## 4. Entity Relationships

```text
Profile ──────────────────────────────────────────────┐
  │                                                    │
  │ 1:1                                                │ 1:N
  ▼                                                    ▼
ProfileResume                                       Company
                                                       │ 1:N
Profile ──── 1:N ──── Application ◄────────────────────┘
                          │
                          ├── 1:1 ──► ApplicationResume
                          ├── 1:N ──► ApplicationAttachment
                          ├── 1:N ──► ApplicationTimeline
                          ├── 1:N ──► ApplicationMetaData
                          └── 1:N ──► InterviewNote
                                          │
                                          └── 1:N ──► InterviewAttachment

Company ──── 1:N ──► CompanyMetaData

Profile ──── 1:N ──► Session
Profile ──── 1:N ──► VerificationCode
```

Key rules:

- Each `Profile` owns all its data. Deleting a profile cascades to applications, companies, sessions, and verification codes.
- `Application` holds the job posting data, all AI-generated text, and all match scores. There is no separate `JobDescription` or `CoverLetter` entity.
- `ApplicationResume` and `ProfileResume` are file-pointer entities (path, content type, length) — not document-content entities.
- `Application` → `Company` is nullable: an application can exist without a tracked company record.
- `ApplicationTimeline` records status transitions; it does not replace the `status` field on `Application`.

---

## 5. Flyway Migration Conventions

All schema changes are managed by Flyway.

### File location

```text
src/main/resources/db/migration/
```

### Naming pattern

```text
V{version}__{description}.sql
```

Examples:

```text
V1__Initial_schema.sql
V2__add_company_logo_column.sql
V3__add_application_salary_column.sql
```

### Rules

- Use sequential integers for version numbers.
- Descriptions should be lowercase with underscores.
- Each migration does one coherent thing.
- Never edit an already-applied migration — create a new one.
- Migrations are additive: add columns or tables, do not destructively drop unless a removal migration is intentional and safe.
- Use explicit column types appropriate for PostgreSQL.

---

## 6. JPA Conventions

### Entity class conventions

- Use `@Entity` and `@Table(name = "...")` with explicit table names.
- Define `@Id` explicitly; do not rely on `@GeneratedValue` without specifying `GenerationType.UUID`.
- Audit timestamps (`createdDate`, `lastModifiedDate`) are `Instant` fields defined in `AuditableEntity` — do not use Hibernate's `@CreationTimestamp` / `@UpdateTimestamp` annotations.
- Avoid using JPA `CascadeType.ALL` carelessly — be explicit about cascade rules.
- Avoid bidirectional relationships unless they are genuinely needed.

### Identity

All entities use `UUID` primary keys generated by JPA via `GenerationType.UUID`. Do not use database sequences or auto-increment integers for new entities.

### Lazy loading

Default all `@ManyToOne` and `@OneToMany` relationships to `LAZY` unless there is a specific performance reason to use `EAGER`.

### Kotlin-specific notes

- Mark entity classes and their JPA-relevant properties as `open` to support Hibernate proxying (handled by the `all-open` Gradle plugin for `@Entity`, `@MappedSuperclass`, `@Embeddable`).
- Most domain entities use Kotlin `data class` with a custom `equals()`/`hashCode()` based on `id` only. Do not rely on the data-class-generated `equals`/`hashCode` for JPA entities.
- Plain `class` is used for entities where copy semantics are not meaningful (e.g. `ApplicationResume`, `CompanyMetaData`).

---

## 7. DTO Conventions

Entities should not be returned directly from controllers.

Use DTOs (data transfer objects) to define the API surface:

- Request DTOs: data coming from the frontend
- Response DTOs: data returned to the frontend

Rules:

- DTOs should not contain JPA annotations.
- DTOs should be defined in a `dto/` package.
- Map between entities and DTOs in a `mapper/` package or a dedicated mapping service.
- Do not expose internal entity IDs in DTOs unless required by the frontend.
- Validate request DTOs using Spring validation annotations.

---

## 8. What Not To Do

- Do not put business logic in JPA entity classes.
- Do not call repositories from controllers.
- Do not return raw entities from API endpoints.
- Do not edit applied Flyway migrations.
- Do not mix domain concerns in a single entity (e.g. do not store resume content and job description content in the same table).
- Do not rely on Hibernate auto-DDL (`spring.jpa.hibernate.ddl-auto`) in production — use Flyway only.

---

## Related Docs

- `ARCHITECTURE.md`
- `DESIGN.md`
- `AGENTS.md`
- `API.md`
- `SECURITY.md`
