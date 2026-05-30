# Plan: Delete Product (with confirmation dialog)

## Goal
Add a **Delete** button to each row of the products table that removes the
product from the database. Before deleting, show a native `<dialog>` to confirm
the action. After deletion, refresh the table in place (HTMX).

## Changes

### Backend
- **`ProductService.kt`** — add `deleteProduct(id)`:
  ```kotlin
  @Transactional
  fun deleteProduct(id: Long) = productRepository.deleteById(id)
  ```
  Variants are removed via the existing `cascade = ALL` / `orphanRemoval` on
  `Product.variants` (and `ON DELETE CASCADE` in the V2 migration).
- **`ProductController.kt`** — add an HTMX endpoint:
  ```kotlin
  @DeleteMapping("/products/{id}")
  fun deleteProduct(@PathVariable id, model): String {
      productService.deleteProduct(id)
      model.addAttribute("products", productService.findAll())
      return "fragments/products :: table"   // refreshed table swapped in
  }
  ```
  (No Spring Security on the classpath, so DELETE needs no CSRF token.)

### Frontend
- **`templates/fragments/products.html`** — in the existing action cell, add a
  **Delete** button next to **Edit**. The button opens a native `<dialog>`
  (its `nextElementSibling`, so no per-row id wiring needed):
  ```html
  <wa-button variant="danger" appearance="outlined" size="small"
             onclick="this.nextElementSibling.showModal()"> … Delete</wa-button>
  <dialog class="delete-dialog">
    <h3>Delete product?</h3>
    <p>Permanently remove “<span th:text="${product.title}"></span>”.</p>
    <div class="dialog-actions">
      <wa-button appearance="outlined"
                 onclick="this.closest('dialog').close()">Cancel</wa-button>
      <wa-button variant="danger"
                 th:attr="hx-delete=@{/products/{id}(id=${product.id})}"
                 hx-target="#products-container" hx-swap="innerHTML">Delete</wa-button>
    </div>
  </dialog>
  ```
  Confirming fires `hx-delete`, which swaps the refreshed table (and all dialogs)
  into `#products-container`; the open dialog is replaced, so it closes itself.
- **`static/app.css`** — add `.delete-dialog` / `.dialog-actions` styling.

## Verification
1. `./gradlew bootRun`.
2. Open `/`, click **Load products**.
3. Click **Delete** on a row → confirmation `<dialog>` appears.
4. **Cancel** closes it with no change; **Delete** removes the product and the
   table refreshes without that row (count badge updates).
5. Reload to confirm the product is gone from the database.
