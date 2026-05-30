package com.example.products

import com.example.products.product.NewProductForm
import com.example.products.product.ProductService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class ProductManagementApplicationTests {

    @Autowired
    private lateinit var productService: ProductService

    @Test
    fun `context loads`() {
    }

    @Test
    fun `adding a product with variant details persists a variant`() {
        val before = productService.findAll().size

        val form = NewProductForm().apply {
            title = "Test Product"
            vendor = "ACME"
            productType = "Test"
            sku = "TEST-1"
            price = "129.90"
        }
        productService.addProduct(form)

        val products = productService.findAll()
        assertThat(products).hasSize(before + 1)
        val saved = products.first { it.title == "Test Product" }
        assertThat(saved.vendor).isEqualTo("ACME")
        assertThat(saved.variants).hasSize(1)
        assertThat(saved.lowestPrice()).isEqualByComparingTo("129.90")
    }
}
