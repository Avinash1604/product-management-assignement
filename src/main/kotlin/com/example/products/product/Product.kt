package com.example.products.product

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product(
    @Column(name = "external_id", unique = true)
    var externalId: Long? = null,

    @Column(nullable = false, length = 512)
    var title: String = "",

    @Column(length = 255)
    var vendor: String? = null,

    @Column(name = "product_type", length = 255)
    var productType: String? = null,

    @Column(length = 512)
    var handle: String? = null,

    @Column(name = "image_url", length = 1000)
    var imageUrl: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(
        mappedBy = "product",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    )
    var variants: MutableList<ProductVariant> = mutableListOf()

    fun addVariant(variant: ProductVariant) {
        variant.product = this
        variants.add(variant)
    }

    /** Lowest variant price, used for display in the products table. */
    fun lowestPrice(): BigDecimal? = variants.mapNotNull { it.price }.minOrNull()

    /** True if any variant is available; used for the stock badge. */
    fun inStock(): Boolean = variants.any { it.available }
}
