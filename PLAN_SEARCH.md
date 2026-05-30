# Plan: Product Search Page (live search by title)

## Goal
A new page with a search box that filters products **by title**, updating the
results table **as you type** (active/live search). Reuses the existing
`fragments/products :: table` fragment and HTMX patterns for a consistent look.

## Changes

### 1. Repository — `ProductRepository.kt`
Add a Spring Data derived query:
```kotlin
fun findByTitleContainingIgnoreCaseOrderByIdDesc(title: String): List<Product>
```

### 2. Service — `ProductService.kt`
```kotlin
@Transactional(readOnly = true)
fun search(query: String?): List<Product> =
    if (query.isNullOrBlank()) findAll()
    else productRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(query.trim())
```

### 3. Controller — `ProductController.kt`
- `GET /search` → returns view `"search"`.
- `GET /products/search?q=...` (HTMX) → model attr `products` = `search(q)`,
  returns `"fragments/products :: table"`.

### 4. Template — `templates/search.html`
Full page mirroring `index.html` head (Web Awesome `dist-cdn/`, HTMX, app.css).
- `<wa-input name="q">` with `hx-get="/products/search"`,
  `hx-trigger="input changed delay:300ms, search"`, target `#search-results`.
- `#search-results` loads all products on `hx-trigger="load"`.
- Nav link back to `/`; add a `/search` link on `index.html`.

### 5. Styling — `app.css`
Reuse existing classes; add minor rules for search box width / nav link.

## Notes
- `title` column already exists (V1 migration) — no DB migration.
- Web Awesome: load from `dist-cdn/`, omit `data-webawesome`.

## Verification
1. `./gradlew bootRun` (Postgres on 5433), ensure products loaded.
2. Open `/search` → table populated on load.
3. Type partial title → filters within ~300ms, no full reload.
4. Non-match → empty-state callout; clear → full list returns.
5. Case-insensitivity + nav links both directions.
