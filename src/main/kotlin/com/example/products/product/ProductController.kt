package com.example.products.product

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

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

    /** The search page: a search box that live-filters the products table by title. */
    @GetMapping("/search")
    fun search(): String = "search"

    /** HTMX endpoint: returns the table fragment filtered by the title query. */
    @GetMapping("/products/search")
    fun searchProducts(@RequestParam(name = "q", required = false) q: String?, model: Model): String {
        model.addAttribute("products", productService.search(q))
        return "fragments/products :: table"
    }

    /** HTMX endpoint: saves a new product, then returns the refreshed table fragment. */
    @PostMapping("/products")
    fun addProduct(@ModelAttribute form: NewProductForm, model: Model): String {
        productService.addProduct(form)
        model.addAttribute("products", productService.findAll())
        return "fragments/products :: table"
    }

    /** The edit page: a form pre-filled with the product's current details. */
    @GetMapping("/products/{id}/edit")
    fun editProduct(@PathVariable id: Long, model: Model): String {
        model.addAttribute("product", productService.findById(id))
        return "edit"
    }

    /** Saves the edited product details, then returns to the products page. */
    @PostMapping("/products/{id}")
    fun updateProduct(@PathVariable id: Long, @ModelAttribute form: EditProductForm): String {
        productService.updateProduct(id, form)
        return "redirect:/"
    }
}
