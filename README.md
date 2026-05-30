# Product Management

A small full-stack demo: a Spring Boot (Kotlin) app that loads products from Postgres and lets you
add new ones, with the table updating **without a full page reload** via [HTMX](https://htmx.org).
UI components use [Web Awesome](https://webawesome.com). The database is seeded at startup by a
scheduled job that ingests products from `https://famme.no/products.json`.

## Tech stack (latest stable)

| Tool | Version |
|------|---------|
| Kotlin | 2.3.21 |
| Spring Boot | 4.0.6 |
| Gradle | 9.5.1 (wrapper) |
| JDK (toolchain) | 25 (Temurin, auto-provisioned) → **bytecode targets Java 21** |
| PostgreSQL | 18 (Docker) |
| Flyway | (managed by Spring Boot BOM) |
| Frontend | HTMX 2 + Web Awesome 3 (via CDN) |

### "Target an earlier JVM, run on a newer one"

The build compiles with **JDK 25** but emits **Java 21 bytecode**, so the artifact runs on any
JVM 21+ while being built with the latest, most efficient JDK. See [build.gradle.kts](build.gradle.kts):

```kotlin
java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }   // compile + run on JDK 25
kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_21 } }           // emit Java 21 bytecode
```

Gradle auto-downloads JDK 25 via the Foojay toolchain resolver (configured in
[settings.gradle.kts](settings.gradle.kts)) — no manual JDK install needed.

Verify:
```bash
./gradlew javaToolchains          # shows JDK 25 "Auto-provisioned by Gradle"
# compiled classes are major version 65 (= Java 21)
```

## Data model

The famme.no JSON is Shopify-style: one product has many variants. We model that relationally with
two tables and a foreign key (see [db/migration](src/main/resources/db/migration)), keeping only a
handful of relevant fields:

- **products**: `external_id` (famme id, for idempotent upserts), `title`, `vendor`, `product_type`,
  `handle`, `image_url`
- **product_variants**: `product_id` (FK), `title`, `sku`, `price`, `available`

## Running locally

Prerequisites: Docker, and any JDK 21+ on your `PATH` (Gradle provisions JDK 25 for the build itself).

```bash
# 1. Start Postgres (host port 5433 -> container 5432)
docker compose up -d

# 2. Run the app (Flyway migrates, then the scheduled job seeds up to 50 products immediately)
./gradlew bootRun

# 3. Open the app
open http://localhost:8080
```

Then click **Load products**, and use the form to add a product — the table updates in place.

> **Port note:** Postgres is published on host port **5433** to avoid clashing with a local
> Postgres on 5432. Connection: `jdbc:postgresql://localhost:5433/products` (user/password `products`).

## How it works

- [`ProductSyncJob`](src/main/kotlin/com/example/products/sync/ProductSyncJob.kt) —
  `@Scheduled(initialDelay = 0, ...)` runs at startup, fetches via
  [`FammeClient`](src/main/kotlin/com/example/products/sync/FammeClient.kt) (RestClient), keeps the
  first 50, and **upserts** by famme id so restarts don't duplicate rows.
- [`ProductController`](src/main/kotlin/com/example/products/product/ProductController.kt) —
  `GET /` renders the page; `GET /products` and `POST /products` return a Thymeleaf **fragment**
  ([fragments/products.html](src/main/resources/templates/fragments/products.html)) that HTMX swaps
  into the table container.

## Testing the HTTP endpoints

Open [http/products.http](http/products.http) in IntelliJ and run the requests with the built-in
HTTP client (no Postman needed). It covers loading the table, adding products, and the upstream
famme.no source.

## Tests

```bash
./gradlew test
```
(Requires the Postgres container to be running.)
