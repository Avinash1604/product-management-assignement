package com.example.products.product

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {

    @Transactional(readOnly = true)
    fun findAll(): List<Product> = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))

    /** Filter products by title (case-insensitive substring). Blank query returns all. */
    @Transactional(readOnly = true)
    fun search(query: String?): List<Product> =
        if (query.isNullOrBlank()) findAll()
        else productRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(query.trim())

    @Transactional(readOnly = true)
    fun findById(id: Long): Product =
        productRepository.findById(id).orElseThrow {
            NoSuchElementException("Product $id not found")
        }

    /** Delete a product (variants cascade via orphanRemoval / ON DELETE CASCADE). */
    @Transactional
    fun deleteProduct(id: Long) = productRepository.deleteById(id)

    /** Update the product-level details (title, vendor, type, image). */
    @Transactional
    fun updateProduct(id: Long, form: EditProductForm): Product {
        val product = findById(id)
        product.title = form.title.trim()
        product.vendor = form.vendor?.trim().orNullIfBlank()
        product.productType = form.productType?.trim().orNullIfBlank()
        product.imageUrl = form.imageUrl?.trim().orNullIfBlank()
        return productRepository.save(product)
    }

    @Transactional
    fun addProduct(form: NewProductForm): Product {
        val product = Product(
            title = form.title.trim(),
            vendor = form.vendor?.trim().orNullIfBlank(),
            productType = form.productType?.trim().orNullIfBlank(),
        )

        // Only attach a variant if the form actually provided variant details.
        val price = form.price?.trim()?.toBigDecimalOrNull()
        if (!form.variantTitle.isNullOrBlank() || !form.sku.isNullOrBlank() || price != null) {
            product.addVariant(
                ProductVariant(
                    title = form.variantTitle?.trim().orNullIfBlank() ?: "Default",
                    sku = form.sku?.trim().orNullIfBlank(),
                    price = price,
                    available = form.available,
                ),
            )
        }

        return productRepository.save(product)
    }

    private fun String?.orNullIfBlank(): String? = this?.takeIf { it.isNotBlank() }
}
