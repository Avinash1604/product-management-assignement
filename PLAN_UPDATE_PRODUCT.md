# Plan: Update Product Details Page

## Goal
Add an **Edit** link to each row of the products table that opens a dedicated
page for updating that product's details, then returns to the products page.

## Changes

### Backend
- **`EditProductForm.kt`** (new) — form backing object for product-level details
  (title, vendor, productType, imageUrl).
- **`ProductService.kt`** — added `findById(id)` and `updateProduct(id, form)`
  (reuses the existing `orNullIfBlank` normalization).
- **`ProductController.kt`**
  - `GET /products/{id}/edit` → renders the pre-filled `edit` page.
  - `POST /products/{id}` → saves and `redirect:/` back to the products page.

### Frontend
- **`templates/fragments/products.html`** — added a trailing column with an
  **Edit** button linking to `/products/{id}/edit`.
- **`templates/edit.html`** (new) — full page mirroring the existing head/header,
  with a `<form method="post">` pre-filled via `th:value` and a "Back to products"
  link. Reuses the `.add-card` / `.form-grid` styles and the same
  `<wa-button type="submit">` pattern as the add form.
- **`static/app.css`** — added `.action-cell` styling.

## Scope notes
- Covers product-level fields (title, vendor, type, image URL). Variant editing
  is not included since the table is product-centric.

## Verification
1. `./gradlew bootRun`.
2. Open `/`, click **Load products**, then **Edit** on any row.
3. Change a field, click **Save changes** → returns to the products page with
   the update persisted.
