# Plan: Stock-status Badge (+ fix search-page delete target)

## Goal
1. Show an **In stock / Sold out** badge per row in the products table, computed
   from existing `ProductVariant.available` data (no DB/backend query changes).
2. Fix a defect: the row **Delete** button hardcodes `hx-target="#products-container"`,
   which doesn't exist on the `/search` page, so delete silently fails there.

## Changes

### 1. `Product.kt` — add helper
```kotlin
/** True if any variant is available; used for the stock badge. */
fun inStock(): Boolean = variants.any { it.available }
```

### 2. `templates/fragments/products.html`
- Add a `<th>Stock</th>` header (before the empty actions column).
- Add a cell rendering a Web Awesome badge:
  ```html
  <td>
      <wa-badge th:if="${product.inStock()}" variant="success" pill>In stock</wa-badge>
      <wa-badge th:unless="${product.inStock()}" variant="danger" pill>Sold out</wa-badge>
  </td>
  ```
- **Delete fix:** change the confirm button's `hx-target="#products-container"`
  to `hx-target="closest section"`. Both pages wrap the table in a `<section
  class="card">` (`#products-container` on `/`, `#search-results` on `/search`),
  so the relative target works on both without knowing the id.

## Notes
- The shared `DELETE /products/{id}` returns the full list (`findAll`), so deleting
  from `/search` refreshes the table with all products (the active filter is
  dropped until the user types again). Acceptable for now.
- No migration, repository, service, or controller changes needed.

## Verification
1. `./gradlew bootRun`, open `/`, **Load products**.
2. Each row shows **In stock** (green) or **Sold out** (red) — products whose
   variants are all unavailable (or have no variants) show Sold out.
3. Delete from `/` still works (table refreshes via `closest section`).
4. Open `/search`, click **Delete** on a row → it now works there too.
