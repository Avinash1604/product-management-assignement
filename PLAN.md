# Product Management Assignment — Implementation Plan

## Context
This is a take-home assignment to demonstrate mastery of modern JVM tooling **and** a small
full-stack feature. The repo is currently empty (only `README.md`, `.gitignore`, `.idea/`).

We must build a Spring Boot web app that:
- Shows a **"Load products"** button → fetches products from **Postgres** → renders a table.
- Has an **add-product form** that saves to the DB and updates the table **without a page reload** (HTMX).
- Is seeded by a **scheduled job** that pulls `https://famme.no/products.json` (limit **50**) at startup
  (`initialDelay=0`).
- Uses **Flyway** migrations, **Web Awesome** components, **HTMX** for partial updates.

Confirmed decisions: **Kotlin**, **separate tables + FK** for variants, **JDK 25 toolchain compiling to
bytecode 21** (the "target earlier, run newer" point), **Docker Compose** Postgres.

The Loom must visibly use IntelliJ's built-in **DB client**, **git client**, and **HTTP client**, and
show project settings proving the latest JVM. A **public GitHub repo** is required.

---

## Tech stack (use latest stable as of build time)
- **Kotlin** (2.x), **Spring Boot** (latest 3.x), **Gradle wrapper** (latest, Kotlin DSL).
- **JDK 25 toolchain** auto-provisioned via the **Foojay** toolchain-resolver plugin; **bytecode target 21**.
- Spring deps: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-thymeleaf`,
  `flyway-core`, `flyway-database-postgresql`, `postgresql` driver, `jackson-module-kotlin`.
- Frontend (no build step): **HTMX** + **Web Awesome** via CDN `<script>`/`<link>` in the Thymeleaf layout.

## Data model (3–5 fields, separate tables)
Keep the Shopify-style JSON lean. Use the famme product/variant **id as natural key** so the scheduled job
can **upsert** (idempotent on restart).

- `products`: `id BIGINT PK`, `title`, `vendor`, `product_type`, `handle`, `image_url` (nullable).
- `product_variants`: `id BIGINT PK`, `product_id BIGINT FK → products(id) ON DELETE CASCADE`,
  `title`, `sku`, `price NUMERIC(10,2)`, `available BOOLEAN`.

Manually added products (via the form) get app-generated ids in a non-colliding range (e.g. negative ids
or a separate sequence) so they don't clash with famme ids.

## JVM "target earlier, run newer" config (`build.gradle.kts`)
```kotlin
java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }   // compile + run on JDK 25
kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_21 } }           // emit Java 21 bytecode
```
`settings.gradle.kts`: apply `id("org.gradle.toolchains.foojay-resolver-convention")` so Gradle
auto-downloads JDK 25. Verify with `./gradlew -q javaToolchains`. In the Loom, show IntelliJ
**Project Structure → SDK = 25** and Gradle JVM = 25.

---

## Files to create

**Build / infra**
- `build.gradle.kts`, `settings.gradle.kts`, `gradle/wrapper/gradle-wrapper.properties` (latest dist).
- `docker-compose.yml` — Postgres (latest), exposed `5432`, db/user/password matching `application.yml`.
- `.gitignore` — ⚠️ current file ignores `*.jar`; **force-add** `gradle/wrapper/gradle-wrapper.jar`
  (`git add -f`) or add a negation rule, else the wrapper breaks for the reviewer.

**App (`src/main/kotlin/<pkg>/`)**
- `Application.kt` — `@SpringBootApplication @EnableScheduling`.
- `product/Product.kt`, `product/ProductVariant.kt` — JPA entities (`@OneToMany` / `@ManyToOne`).
- `product/ProductRepository.kt`, `product/ProductVariantRepository.kt`.
- `product/ProductService.kt` — list products, add product (+ optional first variant from form).
- `product/ProductController.kt` — web endpoints (below).
- `product/sync/FammeProduct.kt` etc. — Jackson DTOs for the JSON (only fields we keep).
- `product/sync/FammeClient.kt` — `RestClient` calling `products.json?limit=250`, `.take(50)`.
- `product/sync/ProductSyncJob.kt` — `@Scheduled(initialDelay = 0, fixedDelay = <large>)`, upserts.

**Resources (`src/main/resources/`)**
- `application.yml` — datasource, JPA `ddl-auto: validate` (Flyway owns schema), Flyway on.
- `db/migration/V1__create_products.sql`, `V2__create_product_variants.sql`.
- `templates/index.html` — header, Web Awesome **Load products** button (`hx-get="/products"`,
  `hx-target="#products-container"`), empty table container, add-product form
  (`hx-post="/products"`, `hx-target="#products-container"`, `hx-swap="innerHTML"`).
- `templates/fragments/products-table.html` — table fragment reused by GET and POST responses.
- `static/app.css` — light styling so "the table is nice".

**Web endpoints (server-rendered HTML fragments for HTMX)**
- `GET /` → full page.
- `GET /products` → products-table fragment (button target).
- `POST /products` → save, then return the **updated** products-table fragment (no page reload).

**IntelliJ HTTP client (version-controlled, in repo)**
- `http/products.http` — requests for `GET /products` and `POST /products` (form-encoded), plus a
  request hitting `famme.no/products.json` to show the source. Demo these in the Loom.

---

## Verification (end-to-end)
1. `docker compose up -d` → Postgres running.
2. `./gradlew -q javaToolchains` → shows JDK 25 provisioned; confirm bytecode 21 target.
3. `./gradlew bootRun` → Flyway applies V1/V2; scheduled job runs immediately and seeds ≤50 products.
4. Open `http://localhost:8080` → click **Load products** → table fills (HTMX, no reload).
5. Submit the **add-product** form → row appears immediately (HTMX swap), no full reload.
6. Confirm persistence in **IntelliJ DB client** (and/or `psql`): rows in `products` / `product_variants`.
7. Run `http/products.http` requests from IntelliJ's **HTTP client**.
8. `./gradlew test` (basic context-loads + a controller/service test).
9. Commit with IntelliJ **git client**; push to a **public GitHub repo**.

## Loom checklist (so the reviewer sees tool mastery)
IntelliJ Ultimate (EAP) · built-in **DB client** for Postgres · built-in **git client** for commits ·
built-in **HTTP client** for requests · **Project Structure** showing JDK 25 / bytecode 21 · the working
web app (load + add) · brief code walkthrough.
