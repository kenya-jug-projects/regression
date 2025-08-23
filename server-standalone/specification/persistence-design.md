# Persistence Design

### Regression

## Overview

This document defines the persistence strategy for the Regression backend system using **SQLite** as the primary datastore. It outlines the schema design for user management, application tracking, log storage, and metadata separation to support efficient AI-based log analysis.

---

## Goals

* Provide a lightweight, file-based persistence layer suitable for development, testing, or embedded deployments.
* Normalize data into logical tables to support future migrations to scalable databases (e.g., PostgreSQL).
* Support efficient querying and AI-driven processing of logs with structured metadata.

---

## Database: SQLite

SQLite is selected for its simplicity, zero-configuration, and embedded nature, ideal for early-stage development and local deployments.

### Key Features:

* ACID-compliant with full SQL support.
* File-based storage (`.db` file).
* No separate server process required.

---

## Tables & Schema

### `users`

Stores authenticated users and access roles.

| Column     | Type                              | Description                           |
|------------|-----------------------------------|---------------------------------------|
| id         | INTEGER PRIMARY KEY AUTOINCREMENT | Unique user ID                        |
| username   | TEXT                              | Unique username                       |
| password   | TEXT                              | Hashed password                       |
| role       | TEXT                              | e.g., `admin`, `viewer`, `datasource` |
| created_at | TEXT                              | ISO timestamp of creation             |

---

### `applications`

Represents applications sending logs.

| Column        | Type                              | Description                   |
|---------------|-----------------------------------|-------------------------------|
| id            | INTEGER PRIMARY KEY AUTOINCREMENT | Unique app ID                 |
| name          | TEXT                              | App name                      |
| version       | TEXT                              | Version string                |
| environment   | TEXT                              | e.g., `production`, `staging` |
| owner_user_id | INTEGER                           | FK to `users.id`              |
| created_at    | TEXT                              | ISO timestamp of registration |

---

### `logs`

Stores core log entries.

| Column         | Type                              | Description                   |
|----------------|-----------------------------------|-------------------------------|
| id             | INTEGER PRIMARY KEY AUTOINCREMENT | Unique log ID                 |
| timestamp      | TEXT                              | ISO 8601 time of log          |
| severity       | TEXT                              | `ERROR`, `WARN`, `INFO`, etc. |
| application_id | INTEGER                           | FK to `applications.id`       |
| source         | TEXT                              | Service/module generating log |
| message        | TEXT                              | Log message                   |

---

### `logs_metadata`

Stores optional or structured metadata associated with logs.

| Column         | Type                              | Description                          |
|----------------|-----------------------------------|--------------------------------------|
| id             | INTEGER PRIMARY KEY AUTOINCREMENT | Metadata ID                          |
| log_id         | INTEGER                           | FK to `logs.id`                      |
| metadata_type  | TEXT                              | Metadata key (e.g., `userId`, `cpu`) |
| metadata_value | TEXT                              | Metadata value (as string)           |
| app_version    | TEXT                              | App Version: value (as string)       |


---

## Relationships

* `logs.application_id` → `applications.id`
* `applications.owner_user_id` → `users.id`
* `logs_metadata.log_id` → `logs.id`

---

## Indexes

* Index on `logs.timestamp` for time-based queries
* Composite index on `logs.application_id`, `severity`
* Index on `logs_metadata.key`, `value` for filtering (optional)

---

## Caching Strategy

* No external cache layer in SQLite configuration.
* In-memory optimization can be achieved using SQLite's `:memory:` mode for ephemeral test instances.
* AI model results or hot queries can be cached at the application level (e.g., using in-memory maps or a Redis cache if extended).

---

## Retention Strategy

* For development: manual log purging or file rotation.
* For production with SQLite: consider archiving old logs periodically via a cron or background job.

---

## Backup & Recovery

* Simple file-level backups of `.db` file.
* Daily snapshots using SQLite's `.backup` API or external tools.
* Recovery involves restoring the file and ensuring write consistency.

---

