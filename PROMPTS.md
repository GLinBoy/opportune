# PROMPTS.md

This document describes the prompt system used in Opportune.

It covers prompt file conventions, template structure, variable contracts, expected inputs and outputs per agent, and rules for updating prompts safely.

Read `AGENTS.md` for the agent catalogue this prompt system serves.

---

## Table of Contents

1. [Overview](#1-overview)
2. [File Conventions](#2-file-conventions)
3. [Template Syntax](#3-template-syntax)
4. [Prompt Catalogue](#4-prompt-catalogue)
5. [Input and Output Contracts](#5-input-and-output-contracts)
6. [Writing Prompts Well](#6-writing-prompts-well)
7. [Updating Prompts](#7-updating-prompts)
8. [Testing a Prompt](#8-testing-a-prompt)
9. [What Not To Do](#9-what-not-to-do)

---

## 1. Overview

Opportune uses Spring AI with the OpenAI adapter to power its AI features.

Prompt templates are stored as plain text files, separate from Java/Kotlin source code. This separation is intentional:

- prompts can be reviewed and edited without touching compiled code
- prompts can be version-controlled independently of logic
- prompts are readable by humans without a development environment
- prompt changes can be documented and audited in this file

The prompt system is currently responsible for:

- **Application Analysis**: job match scoring, cover letter generation, resume improvement insights, interview self-introduction, and metadata extraction — all triggered asynchronously when a job application is submitted

See `AGENTS.md` for the full agent catalogue, including agents that are planned but not yet implemented.

---

## 2. File Conventions

### Location

All prompt files live under:

```text
src/main/resources/prompts/
```

### File extension

Use the `.st` extension, which is the Spring AI / StringTemplate convention.

### Naming

Use lowercase kebab-case names that describe the agent or task. For agents that use a system/user split, suffix the files with `-system` and `-user`:

```text
ai-analysis-system.st
ai-analysis-user.st
```

### Spring Boot loading

Prompt files are loaded using `ClassPathResource` directly in the service:

```kotlin
// User prompt: use PromptTemplate for variable substitution
val userPromptTemplate = PromptTemplate(ClassPathResource("prompts/ai-analysis-user.st"))
val userMessage = userPromptTemplate.createMessage(
    mapOf(
        "cleanJobDescription" to cleanJobDescription,
        "extractedResumeText" to extractedResumeText
    )
)

// System prompt: read as plain text when the prompt contains JSON schema
// (curly braces in the schema would be misinterpreted by PromptTemplate)
val systemPromptText = ClassPathResource("prompts/ai-analysis-system.st")
    .inputStream.bufferedReader().readText()
    .replace("{currentDate}", currentDate)
```

> **Important:** If a system prompt embeds a JSON schema (which contains `{` and `}` characters), do **not** load it through `PromptTemplate` — the parser will treat those braces as variable placeholders. Read the file as raw text and use `String.replace()` for any genuine variable substitutions.

---

## 3. Template Syntax

Prompt files use the StringTemplate syntax for variable substitution.

Variables are wrapped in curly braces:

```text
{variableName}
```

Example:

```text
You are a professional resume writer.

The candidate's background:
{userProfile}

The job posting they are applying to:
{jobDescription}

Tailor the resume to this job. Return the result as structured markdown.
```

### Variable injection in code

Variables are passed as a map when calling the template. Use `.createMessage()` to get a `Message` object for use with `ChatClient`:

```kotlin
val userPromptTemplate = PromptTemplate(ClassPathResource("prompts/ai-analysis-user.st"))
val userMessage = userPromptTemplate.createMessage(
    mapOf(
        "cleanJobDescription" to cleanJobDescription,
        "extractedResumeText" to extractedResumeText
    )
)

chatClient.prompt()
    .system(systemPromptText)
    .user(userMessage.text.orEmpty())
    .call()
    .content()
```

### Whitespace and formatting

Keep prompts readable. Use blank lines to separate sections. Do not compress everything into one paragraph — readability for humans reviewing prompts matters as much as the model parsing them.

---

## 4. Prompt Catalogue

### ai-analysis-system.st + ai-analysis-user.st ✅

**Agent:** Application Analysis Agent (see `AGENTS.md` §9.1)

**Purpose:** Analyses a submitted job application against the candidate's resume. Produces a structured JSON response containing match scores, a cover letter, resume improvement insights, an interview self-introduction, job metadata, and a short role description.

**Trigger:** Fired asynchronously by `AiServiceImpl.onApplicationSubmitted()` after an `ApplicationSubmittedEvent` is published (i.e., after `POST /api/applications/submit-url` persists the application).

**System prompt** (`ai-analysis-system.st`)

Contains the full agent role definition, JSON safety rules, writing style rules, field-level output schema definitions, and score calibration guidance. Loaded as **raw text** (not via `PromptTemplate`) because the embedded JSON schema contains curly braces.

| Variable        | Injection method          | Description                                |
| --------------- | ------------------------- | ------------------------------------------ |
| `{currentDate}` | Manual `String.replace()` | Today's date formatted as `"May 26, 2026"` |

**User prompt** (`ai-analysis-user.st`)

A short template that supplies the two runtime inputs. Loaded via `PromptTemplate.createMessage()`.

| Variable              | Type   | Source                                                        |
| --------------------- | ------ | ------------------------------------------------------------- |
| `cleanJobDescription` | String | Job description HTML stripped via Jsoup                       |
| `extractedResumeText` | String | Plain text extracted from the profile's PDF resume via pdfbox |

**Expected output:** A single JSON object. Several string fields within the JSON contain Markdown. The full schema is defined in `ai-analysis-system.st`.

Top-level output fields:

| Field                    | Type              | Description                                                                                                         |
| ------------------------ | ----------------- | ------------------------------------------------------------------------------------------------------------------- |
| `title`                  | String            | Job title extracted from posting                                                                                    |
| `company_name`           | String            | Hiring company name                                                                                                 |
| `location`               | String            | Job location                                                                                                        |
| `short_description`      | String (Markdown) | 3–5 sentence role summary                                                                                           |
| `cover_letter`           | String (Markdown) | Complete, ready-to-send cover letter                                                                                |
| `resume_insights`        | String (Markdown) | Actionable resume improvement guide                                                                                 |
| `interview_introduction` | String (Markdown) | 150–200 word spoken self-introduction                                                                               |
| `resume_match`           | Object            | Nested scoring object with `overall_score`, `skills_match`, `experience_match`, `education_match`, `keywords_match` |
| `metadata`               | Array             | Key-value pairs for additional job details (employment type, seniority, remote policy, etc.)                        |

**Notes:**

- The service strips any accidental ` ```json ` fences from the raw model response before parsing with Jackson
- The raw JSON response is also written to `{files.base-path}/ai-analysis/<applicationId>_<timestamp>.json` for debugging
- If a scoring dimension cannot be assessed, the score is set to `null` and the rationale explains why

---

### document-parser-system.st + document-parser-user.st ✅

**Agent:** Document Parser Agent (see `AGENTS.md` §9.4)

**Purpose:** Extracts structured work experience, education, and skills from a PDF resume. Returns a JSON preview that is never auto-saved — the user reviews and confirms before data is persisted.

**Trigger:** User clicks "Extract from Resume" in the frontend, which calls `POST /api/profiles/resume/extract`. The endpoint returns the preview immediately; data is only saved when the user calls individual CRUD endpoints from the frontend dialog.

**System prompt** (`document-parser-system.st`)

Contains the agent role definition, JSON output schema (matching the internal DTOs), JSON safety rules (escaping, no literal newlines), and the "do not invent data" constraint. Loaded as **raw text** (not via `PromptTemplate`) because the embedded JSON schema contains curly braces.

**Variables:** None (no runtime variable substitution needed).

**User prompt** (`document-parser-user.st`)

A short template that wraps the resume text in `---BEGIN RESUME TEXT---` and `---END RESUME TEXT---` delimiters for prompt injection defence.

| Variable      | Type   | Source                                    |
| ------------- | ------ | ----------------------------------------- |
| `resumeText`  | String | Plain text extracted via `PdfResumeTextExtractorImpl` |

**Expected output:** A single JSON object matching `ResumeExtractionResultDTO`:

| Field             | Type              | Description                                        |
| ----------------- | ----------------- | -------------------------------------------------- |
| `work_experiences` | Array             | List of work experiences with job title, company, dates, bullets |
| `education`       | Array             | List of education entries with school, degree, field, dates, courses |
| `skill_groups`    | Array             | List of skill categories with individual skills    |

**Service:** `DocumentParserServiceImpl.parseResumeData()`

**Endpoint:** `POST /api/profiles/resume/extract`

**Notes:**

- The service strips any accidental ` ```json ` fences from the raw model response before parsing
- Returns 404 if the current user has not uploaded a resume
- The `extractText(inputStream, contentType)` method selects a `ResumeTextExtractor` implementation based on content type — currently only `PdfResumeTextExtractorImpl` is available

### Planned prompt files

These files do not yet exist. They correspond to planned agents in `AGENTS.md`:

| File                          | Agent                                 | Notes             |
| ----------------------------- | ------------------------------------- | ----------------- |
| `resume-builder.st`           | Resume Builder Agent (§9.2)           | Streaming via SSE |
| `cover-letter.st`             | Cover Letter Agent (§9.3)             | Streaming via SSE |
| `job-description-analyser.st` | Job Description Analyser Agent (§9.5) | Structured output |

---

## 5. Input and Output Contracts

### Inputs

Every prompt should receive only what it needs.

- Do not pass the full user object into a prompt
- Sanitize and normalize inputs before passing them
- Strip HTML from scraped job descriptions before passing as `jobDescription`
- Validate that required variables are non-empty before calling the template

### Outputs

Output format depends on the agent:

- **Structured agents** (e.g., Application Analysis): return a **JSON object**. Individual fields within the JSON may contain Markdown strings. The service parses the JSON via Jackson before storing results.
- **Streaming agents** (planned): return **Markdown** streamed token-by-token via Server-Sent Events.

For JSON-output agents, the service must strip any accidental ` ```json ` fences from the raw model response before parsing:

````kotlin
val cleanedJson = jsonResponse
    ?.trim()
    ?.removePrefix("```json")
    ?.removePrefix("```")
    ?.removeSuffix("```")
    ?.trim()
    ?: "{}"
````

For Markdown-output agents:

- output should be human-readable and directly editable
- structure output with clear headings so it renders well in `md-editor-v3` / `marked`

### Output handling in services

The raw model response should be validated before reaching the frontend:

- check that output is non-empty
- check for obvious failure responses
- log unexpected outputs for debugging
- do not return raw AI errors to the user

---

## 6. Writing Prompts Well

These guidelines apply to any prompt in this codebase.

### Be specific about the role

Start every prompt with a clear role statement. Example:

```text
You are a professional career coach and resume writer with experience across technical and non-technical roles.
```

### Be specific about the task

State what the model should do, not just what context it has. Example:

```text
Your task is to tailor the candidate's resume to the specific job posting below.
Focus on relevant skills, quantifiable achievements, and language that mirrors the job description.
```

### Be specific about the output format

State the expected format explicitly. Example:

```text
Return your response as markdown with the following sections:
- Professional Summary
- Key Skills
- Work Experience
- Education
```

### Set constraints

Tell the model what to avoid. Example:

```text
Do not invent skills or experience not present in the candidate's profile.
Do not include personal contact details.
Keep the output under 600 words unless the role specifically requires a longer document.
```

### Keep user data isolated

Use variables to inject user data rather than building prompts through string concatenation in code. This keeps prompts readable and avoids accidental injection issues.

---

## 7. Updating Prompts

### When to update a prompt

Update the relevant `.st` file when:

- the task scope changes
- output quality is consistently poor
- a new variable is needed
- output format requirements change
- user feedback reveals a systematic issue

### How to update a prompt

1. Edit the `.st` file in `src/main/resources/prompts/`
2. Update the input variable table in this document if variables changed
3. Update the expected output description if the output format changed
4. If behavior changed significantly, note it in `AGENTS.md` as well
5. Test the change manually before committing

### Versioning

Prompt files are versioned by Git. Do not create `resume-tailor-v2.st` style parallel files. Make changes in place and rely on Git history to track prompt evolution.

If a prompt needs a fundamentally different behavior, consider whether it should be a new agent in `AGENTS.md` rather than a replacement of an existing prompt.

---

## 8. Testing a Prompt

There is no automated test runner for prompts. Test them manually.

A minimal test checklist for any prompt change:

- [ ] The prompt renders without template errors
- [ ] All variables are substituted correctly
- [ ] The output is in the expected format
- [ ] The output is grounded in the input data
- [ ] The output is human-readable and editable
- [ ] Edge cases were tested: short profiles, long job descriptions, missing optional fields

Keep representative test inputs in a local scratch file during development. Do not commit test prompt inputs with real user data.

---

## 9. What Not To Do

Avoid these patterns:

- **Building prompts by string concatenation in code** — use `.st` template files
- **Hardcoding model-specific instructions** — keep prompts model-agnostic where possible
- **Trusting raw AI output without validation** — always check before forwarding to the frontend
- **Creating parallel versioned prompt files** (`-v2`, `-new`, `-test`) — use Git history
- **Hiding prompt logic across many service methods** — prompts should live in `.st` files, not be assembled from fragments scattered across code
- **Passing the full user domain object into a prompt** — extract only the fields the prompt needs
- **Returning AI errors directly to the user** — handle failures gracefully in the service layer

---

## Related Docs

- `AGENTS.md` — the AI agent catalogue that consumes these prompts
- `DESIGN.md` — the AI data flow in the system architecture
- `AI_CONTEXT.md` — the compact project context for AI tools
- `ARCHITECTURE.md` — backend service layer where prompts are executed
