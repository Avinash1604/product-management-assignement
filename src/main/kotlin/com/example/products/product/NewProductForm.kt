package com.example.products.product

/**
 * Backing object for the "add product" HTML form. Field names map to the form inputs.
 * `price` is kept as a String so an empty input binds cleanly (parsed in the service).
 */
class NewProductForm {
    var title: String = ""
    var vendor: String? = null
    var productType: String? = null

    // Optional single starter variant entered through the form.
    var variantTitle: String? = null
    var sku: String? = null
    var price: String? = null
    var available: Boolean = true
}
