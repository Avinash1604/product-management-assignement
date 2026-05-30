package com.example.products.product

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class ProductController(
    private val productService: ProductService,
) {

    /** The single page: header, "Load products" button, table container and add-product form. */
    @GetMapping("/")
    fun index(): String = "index"

    /** HTMX endpoint: returns just the table fragment so it can be swapped into the page. */
    @GetMapping("/products")
    fun listProducts(model: Model): String {
        model.addAttribute("products", productService.findAll())
        return "fragments/products :: table"
    }

    /** HTMX endpoint: saves a new product, then returns the refreshed table fragment. */
    @PostMapping("/products")
    fun addProduct(@ModelAttribute form: NewProductForm, model: Model): String {
        productService.addProduct(form)
        model.addAttribute("products", productService.findAll())
        return "fragments/products :: table"
    }
}
