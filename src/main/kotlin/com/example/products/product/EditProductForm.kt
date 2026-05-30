package com.example.products.product

/**
 * Backing object for the "edit product" HTML form. Covers the product-level
 * details (not variants), which are edited on a dedicated page.
 */
class EditProductForm {
    var title: String = ""
    var vendor: String? = null
    var productType: String? = null
    var imageUrl: String? = null
}
